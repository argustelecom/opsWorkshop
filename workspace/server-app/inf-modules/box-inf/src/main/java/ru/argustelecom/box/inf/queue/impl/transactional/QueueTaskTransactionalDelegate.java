package ru.argustelecom.box.inf.queue.impl.transactional;

import static ru.argustelecom.box.inf.queue.impl.transactional.ExecutionStatus.EXECUTED_SUCCESSFULLY;
import static ru.argustelecom.box.inf.queue.impl.transactional.ExecutionStatus.EXECUTED_WITH_ERRORS;
import static ru.argustelecom.box.inf.queue.impl.transactional.ExecutionStatus.EXECUTION_FAILED;
import static ru.argustelecom.box.inf.queue.impl.transactional.ExecutionStatus.INVAIN;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.transaction.TransactionalException;

import org.jboss.logging.Logger;

import ru.argustelecom.box.inf.queue.api.model.QueueStatus;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;
import ru.argustelecom.box.inf.queue.impl.QueueBusinessException;
import ru.argustelecom.box.inf.queue.impl.QueueErrorServiceImpl;
import ru.argustelecom.box.inf.queue.impl.QueueHistoryServiceImpl;
import ru.argustelecom.box.inf.queue.impl.QueuePoisonedException;
import ru.argustelecom.box.inf.queue.impl.QueueRepositoryImpl;
import ru.argustelecom.box.inf.queue.impl.QueueSystemException;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventErrorImpl;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;
import ru.argustelecom.box.inf.queue.impl.model.QueueImpl;
import ru.argustelecom.box.inf.queue.impl.request.QueueRequest;
import ru.argustelecom.box.inf.queue.impl.task.AbstractQueueTask;
import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties.CDINotActiveException;
import ru.argustelecom.system.inf.utils.CDIHelper;

@Dependent
@Transactional
public class QueueTaskTransactionalDelegate {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private QueueRequest request;

	@Inject
	private QueueRepositoryImpl queueRp;

	@Inject
	private QueueErrorServiceImpl errorSvc;

	@Inject
	private QueueHistoryServiceImpl historySvc;

	private ExecutionContex executionContext;

	@PostConstruct
	protected void postConstruct() {
		log.debugv("Создан транзакционный делегат для потока {0}", Thread.currentThread().getName());
	}

	@PreDestroy
	protected void preDestroy() {
		log.debugv("Удаляется транзакционный делегат для потока {0}", Thread.currentThread().getName());
	}

	public ExecutionStatus tryExecute(AbstractQueueTask task) {
		executionContext = new ExecutionContex();
		ExecutionStatus executionStatus = EXECUTED_SUCCESSFULLY;

		try {

			executionStatus = executeInAutonomousTx(task);

		} catch (QueueBusinessException e) {

			// Все пометки об ошибках уже выполнены в автономной транзакции до снятия лока с очереди
			log.warnv(e, "Во время обработки события произошло ожидаемое исключение {0}:{1}.", e, e.getMessage());
			executionStatus = EXECUTED_WITH_ERRORS;

		} catch (QueuePoisonedException e) {

			// Все пометки об ошибках уже выполнены в автономной транзакции до снятия лока с очереди
			log.warnv(e, "Во время обработки события произошло отравляющее исключение {0}:{1}.", e, e.getMessage());
			executionStatus = EXECUTED_WITH_ERRORS;

		} catch (TransactionalException e) {

			// Попытка обработать исключительный случай, когда работа успешно выполнена, флаш в БД сделан, однако
			// транзакция все равно не закоммитилась. Это могло произойти, например, если произошла ошибка в одном из
			// возможных транзакционных CDI Event, EntityListener, Transaction Synchronization и т.д.
			// Находясь здесь мы уже отпустили лок с события, однако изменения, которые мы сделали все равно не
			// сохранились в БД и следующий поток может уже захватить это событие в то время, пока мы его тут обновляем.
			// В целом, ничего страшного произойти не должно, существует очень маленькая вероятность, что повторая
			// попытка обработать события не провалится (т.е. завершится успехом) и статус этого успешного события будет
			// перезатерт этим обработчиком. Но даже в этом случае должно сломаться только одно событие, а не вся
			// очередь. Благодаря записи в queue_log мы сможем разобраться в этой ситуации.

			log.warnv(e, "Во время коммита транзакции произошло исключение {0}:{1}.", e, e.getMessage());
			executionStatus = ExecutionStatus.EXECUTION_FAILED;
			if (executionContext.getEvent() != null) {
				if (executionContext.getExecutionType() == ExecutionType.WORK_HANDLING) {
					executionStatus = safeMarkUnresolved(executionContext.getEvent(), e, EXECUTED_WITH_ERRORS);
				}
				if (executionContext.getExecutionType() == ExecutionType.ERROR_HANDLING) {
					executionStatus = safeMarkPoisoned(executionContext.getEvent(), e, EXECUTED_WITH_ERRORS);
				}
				executionStatus = safeLogError(executionContext.getEvent(), e, executionStatus);
			}

		} catch (Exception e) {

			// Все пометки об ошибках уже выполнены в автономной транзакции до снятия лока с очереди. По крайней мере мы
			// пытались там это сделать...
			log.fatalv(e, "Во время обработки события произошло неожиданное исключение {0}:{1}.", e, e.getMessage());
			executionStatus = EXECUTION_FAILED;

		}

		executionContext = null;
		return executionStatus;
	}

	@Transactional(value = TxType.REQUIRES_NEW, rollbackOn = Throwable.class)
	protected ExecutionStatus executeInAutonomousTx(AbstractQueueTask task)
			throws QueueSystemException, QueuePoisonedException, QueueBusinessException {

		QueueEventImpl event = null;
		ExecutionStatus executionStatus = EXECUTED_SUCCESSFULLY;

		try {
			// Извлекаем событие из очереди...
			event = (QueueEventImpl) queueRp.poll();

			if (event != null) {
				log.infov("Получили событие для обработки: {0}", event);
				executionContext.setEvent(event);
				try {
					// перед вызовом бизнес-кода мы должны обеспечить наличие RequestContext, т.к. бизнес-код может
					// захотеть получить доступ к бинам в этом контектсе. Этот метод умышленно вызывается до форка, т.к.
					// может бросить QueueSystemException, в результате чего нужно будет приостановить очередь. Если бы
					// мы сначала сделали форк и не смогли обеспечить реквест, то наша очередь вошла бы в мертвую петлю
					// всегда порождая новые потоки и тут же умирая
					request.begin();

					// Если событие есть, то перед его обработкой нужно попробовать размножить обработчик
					// За корректное размножение отвечает пул потоков. Ожидается, что этот метод никогда не выбросит
					// исключение. Для осознания механизма размножения обработчиков, смотри комментарии к методу fork()
					task.fork();

					QueueEventErrorImpl error = (QueueEventErrorImpl) event.getLastError();
					if (error == null || (error.isResolved() && !error.isPoison())) {
						log.debug(
								"Событие новое либо содержит решенную ошибку. Попытка обработать в стандартном режиме");

						try {
							// Ошибки нет или она есть и решена обработчиком бизнес-исключений и при этом не является
							// отравляющей. Будем обрабатывать сообщение в стандартном режиме обычным хэндлером
							executionContext.setExecutionType(ExecutionType.WORK_HANDLING);

							QueueHandlingResult result = handleWork(event);
							if (result == QueueHandlingResult.SUCCESS) {
								historySvc.successInCurrentTx("WORK_HANDLED", event);
							} else {
								historySvc.successInCurrentTx("WORK_SKIPPED", event);
							}

						} catch (QueueSystemException | QueueBusinessException e) {
							// Пометить событие как нерешенное (если исключение уже произошло) нужно в автономной
							// транзакции, но в то время, пока очередь еще залочена. Иначе другой поток может успеть
							// схватить это событие, пока мы успеем вызвать метод markUnresolved из tryExecute
							errorSvc.markUnresolvedInAutonomousTx(event, e);
							throw e;

						} catch (QueuePoisonedException e) {
							// Отравление очереди нужно обрабатывать в автономной транзакции но в то время, пока очередь
							// еще залочена. Иначе другой поток может успеть схватить это событие, пока мы успеем
							// вызвать метод markPoison из tryExecute
							errorSvc.markPoisonedInAutonomousTx(event, e);
							throw e;
						}

					} else if (!error.isPoison()) {
						log.debug("Событие содержит нерешенную ошибку. Попытка обработать бизнес-исключение");
						executionContext.setExecutionType(ExecutionType.ERROR_HANDLING);

						// Ошибка есть, она не решена и еще не признана отравляющей для очереди. Будем обрабатывать
						// ее в режиме бизнес-исключений через соответствующий метод хэндлера. Но в него мы
						// попадем только в том случае, если не превысили максимально допустимое количество попыток
						// обработки этого события в прошлом
						if (error.getAttemptsCount() > MAX_ATTEMPS_COUNT) {
							log.warn("Превышено количество попыток обработки события. Очередь изымается из обработки");

							// Если количество попыток превышено, то мы пометим событие как отравляющее, потому что оно,
							// скорее всего, никогда уже не обработается. Сразу же и обрабатываем его как отравляющее,
							// чтобы сэкономить один цикл обработки
							Throwable maxAttemptsException = getMaxAttemptsException();
							errorSvc.markPoisonedInCurrentTx(event, maxAttemptsException);
							handlePoison(event);

							historySvc.errorInCurrentTx("TOO_MANY_ERRORS", event, maxAttemptsException);

						} else {

							try {
								// Максимальное количество попыток еще не превышено, поэтому можно попробовать
								// обработать последнее исключение события в обработчике бизнес-исключений
								QueueErrorResult resolution = handleError(event, error);
								historySvc.successInCurrentTx("ERROR_HANDLED:" + resolution.name(), event);

							} catch (QueuePoisonedException e) {
								// Отравление очереди нужно обрабатывать в автономной транзакции но в то время, пока
								// очередь еще залочена. Иначе другой поток может успеть схватить это событие, пока мы
								// успеем вызвать метод markPoison из tryExecute
								errorSvc.markPoisonedInAutonomousTx(event, e);
								throw e;
							}
						}
					} else {
						log.warnv("Событие содержит неразрешимую ошибку {0}. Очередь изымается из обработки",
								error.getErrorText());

						// Ошибка есть, мы честно пытались ее обработать как бизнес исключение (либо мы сразу пометили
						// ошибку как ядовитую), но она оказалась неразрешимой (т.е. ядом для очереди, что мы поняли в
						// процессе ее обработки -- бизнес-код не смог вынести решение или завершился исключением), то
						// такое событие мы помечаем как безусловно зафейленное (некий аналог dead messages queue из
						// JMS). Для его решения, возможно, нужно выпустить патч или попробовать обработать в ручном
						// режиме, т.е. в любом случае, очередь сама не сможет разобраться, что с ним делать.
						executionContext.setExecutionType(ExecutionType.POISON_HANDLING);
						handlePoison(event);
						historySvc.successInCurrentTx("POISON_HANDLED", event);
					}
				} finally {

					// Что бы ни случилось, мы должны попытаться закрыть реквест для того, чтобы избежать утечки
					// ресурсов и, как следствие, возможных OutOfMemoryError
					request.end();
				}
			} else {
				log.debug("Не смогли получить новое событие для обработки, очередь пуста");

				// Сюда мы попадаем в том случае, если не смогли получить событие из очереди. Это значит только то, что
				// событий в очереди не осталось и мы работали вхолостую. Поэтому мы должны вернуть соответствующий
				// статус для того, чтобы таск впоследствии решил, что ему делать - продолжать работу вхолостую (если
				// это вечноживущий таск) или умереть (если это временный таск)
				executionStatus = INVAIN;
			}
		} catch (Exception e) {
			if (event != null) {
				historySvc.errorInAutonomousTx("EXECUTION_FAILED", event, e);
			}
			throw e;
		}

		return executionStatus;
	}

	private QueueHandlingResult handleWork(QueueEventImpl event)
			throws QueueSystemException, QueuePoisonedException, QueueBusinessException {

		try {
			// Поиск бизнес-хэндлера для обработки события
			QueueHandler handler = lookupHandler(event);

			// Передача управления по обработке события бизнес-коду
			QueueHandlingResult result = handler.handleWork(event);
			em.flush();

			log.debugv("Результат выполнения: {0}", result);

			QueueImpl queue = (QueueImpl) event.getQueue();
			switch (result) {

			// Событие не выполнено и должно быть повторено через 5 минут (не расходуется попытка выполнения)
			case REPEAT_5: {
				Date newTime = new Date(System.currentTimeMillis() + 5 * 60 * 1000);
				queue.setScheduledTime(newTime);
				queueRp.update(event);

				log.debugv("Для события {0} установлено новое время обработки {1}", event, newTime);
				break;
			}

			// Событие не выполнено и должно быть повторено через 1 минуту (не расходуется попытка выполнения)
			case REPEAT_1: {
				Date newTime = new Date(System.currentTimeMillis() + 1 * 60 * 1000);
				queue.setScheduledTime(newTime);
				queueRp.update(event);

				log.debugv("Для события {0} установлено новое время обработки {1}", event, newTime);
				break;
			}

			// Событие выполнено успешно и может быть исключено из очереди на выполнение
			case SUCCESS: {
				// В случае успешной обработки события его нужно выкинуть из очереди. История о нем останется только в
				// логе. При последующей обработке хэндлеры будут брать новое событие (если оно есть)
				queueRp.remove(event);

				log.infov("Cобытие успешно обработано и исключено из очереди: {0}", event);
				break;
			}

			default: {
				throw new QueuePoisonedException("Неизвестный статус выполнения: " + result);
			}

			}

			log.infov("Событие {0} успешно обработано с результатом: {1}", event, result);
			return result;

		} catch (QueueSystemException | QueuePoisonedException e) {

			// Если произошло системное исключение очереди во время обработки события, то его нужно просто
			// пробросить наверх для временной приостановки работы очереди

			// Если произошло отравление очереди (например, не залукапился хендлер при живом CDI, что является ошибкой
			// разработки и, скорее всего, только для текущего типа события, т.е. некорректно считать, что все остальные
			// типы событий также не смогут залукапить хендлер), то исключение нужно пробросить наверх для того, чтобы
			// екзекутор пометил очередь как мертвую и не пытался больше ее обработать
			throw e;

		} catch (Exception e) {

			// Если при обработке события произошло любое необработанное исключение, отличное от системного исключения
			// или исключения, отравляющего очередь, то будем считать его за бизнес-исключение - т.е. пометим его как
			// нерешенное и попытаемся решить в хэндлере
			throw new QueueBusinessException(e);

		}
	}

	private QueueErrorResult handleError(QueueEventImpl event, QueueEventErrorImpl error)
			throws QueueSystemException, QueuePoisonedException {

		try {
			// Поиск бизнес-хэндлера для обработки исключения
			QueueHandler handler = lookupHandler(event);

			// Передача управления по обработке исключения бизнес-коду
			QueueErrorResult result = handler.handleError(event, error);
			em.flush();

			log.debugv("Принято решение: {0}", result);

			QueueImpl queue = (QueueImpl) event.getQueue();
			switch (result) {

			// Фейлим очередь. Вдальнейшем ее судьбой будет заниматься пользователь
			case FAIL_QUEUE: {
				queue.setStatus(QueueStatus.FAILED);
				queueRp.update(event);

				log.debugv("Очередь {0} помечена как зафейленная", queue);
				break;
			}

			// Выкидываем это событие из очереди. Это значит, что событие либо было не важным (или стало
			// неактуальным) или вместо него зашедулено новое событие с учетом произошедшего исключения
			case REJECT_EVENT: {
				queueRp.remove(event);

				log.debugv("Событие {0} исключено из очереди", event);
				break;
			}

			// Бизнес-код не знает, что делать с этим событием и решает, что необходимо вмешательство пользователей.
			// Для этого событие извлекается из очереди, но не со статусом "зафейлено", а со статусом "неактивно",
			// потому что нам очень интересно различать, почему очередь решила больше не обрабатывать это событие
			// автоматически
			case PROCESS_MANUALLY: {
				queue.setStatus(QueueStatus.INACTIVE);
				queueRp.update(event);

				log.debugv("Очередь {0} помечена как неактивная (требует ручной обработки)", queue);
				break;
			}

			// Для некоторых типов ошибок мы знаем, что можно попробовать повторить операцию немедленно (например,
			// при возникновении оптимистической блокировки)
			case RETRY_IMMEDIATE: {
				// При обработке события пользователь мог поменять что-нибудь, например, контекст. Нужно поддержать
				// эти изменения и не потерять их
				queueRp.update(event);
				errorSvc.markResolvedInCurrentTx(event);

				log.debugv("Событие {0} будет обработано при первой возможности", event);
				break;
			}

			// Для некоторых типов ошибок мы знаем, что можно попробовать повторить операцию несколько позже
			// (например, при SocketTimeout или ConnectionRefused). Немедленное повторение приведет к той же самой
			// ошибке, а если немного подождать, то все будет норм (сервис снова станет доступен, например). Для
			// определения времени ожидания будет использована стратегия условного экспоненциального роста времени
			// ожидания перед повтором
			case RETRY_LATER: {
				Date scheduledTime = resolveNextExecutionTime(event, error);
				queue.setScheduledTime(scheduledTime);
				queueRp.update(event);
				errorSvc.markResolvedInCurrentTx(event);

				log.debugv("Событие {0} будет обработано не ранее чем {1}", event, scheduledTime);
				break;
			}

			default: {
				throw new QueuePoisonedException("Неизвестный Resulution: " + result);
			}

			}

			log.infov("Исключение {0} события {1} успешно обработано с резолюцией: {2}", error, event, result);
			return result;

		} catch (QueueSystemException | QueuePoisonedException e) {

			// Если произошло системное исключение очереди во время обработки бизнес-исключения очереди, то его нужно
			// пробросить наверх для временной приостановки работы очереди

			// Если произошло отравление очереди (например, не залукапился хендлер при живом CDI, что является ошибкой
			// разработки и, скорее всего, только для текущего типа события, т.е. некорректно считать, что все остальные
			// типы событий также не смогут залукапить хендлер), то исключение нужно пробросить наверх для того, чтобы
			// экзекутор пометил очередь как мертвую и не пытался больше ее обработать
			throw e;

		} catch (Exception e) {

			// Если при обработке бизнес-исключения произошло любое необработанное исключение, то с текущей очередью
			// скорее всего не получится ничего сделать ни сейчас, ни после. Поэтому такое исключение считается
			// отравляющим и инициирует изъятие события из обработки
			throw new QueuePoisonedException(e);

		}
	}

	private void handlePoison(QueueEventImpl event) throws QueueSystemException {
		// Изымает ядовитую очередь из обработки для того, чтобы очередь не пыталась вечно обработать заведомо
		// мертвое событие
		QueueImpl queue = (QueueImpl) event.getQueue();
		queue.setStatus(QueueStatus.FAILED);
		queueRp.update(event);

		log.infov("Событие {0} помечено как мертвое и более не будет обрабатываться", event);
	}

	private QueueHandler lookupHandler(QueueEventImpl event) throws QueueSystemException, QueuePoisonedException {
		try {

			QueueHandler worker = CDIHelper.lookupCDIBean(QueueHandler.class, event.getHandlerName());
			log.debugv("Найден обработчик {0} для события {1}", worker, event);

			return worker;

		} catch (CDINotActiveException e) {

			log.errorv(e, "CDI недоступен. Не смогли определить хэндлер для события {0}", event);
			throw new QueueSystemException(e);

		} catch (Exception e) {

			log.errorv(e, "Не смогли определить хэндлер для события {0}", event);
			throw new QueuePoisonedException(e);

		}
	}

	private Date resolveNextExecutionTime(QueueEventImpl event, QueueEventErrorImpl error) {
		int attemptNo = error.getAttemptsCount() > 0 ? error.getAttemptsCount() - 1 : 0;
		long delay = attemptNo < DELAY_MILLIS.length ? DELAY_MILLIS[attemptNo] : DEFAULT_DELAY_MILLIS;
		return new Date(System.currentTimeMillis() + delay);
	}

	private Throwable getMaxAttemptsException() {
		return new QueuePoisonedException("Превышено максимальное число попыток обработки события");
	}

	private ExecutionStatus safeMarkUnresolved(QueueEventImpl event, Throwable exception, ExecutionStatus onSuccess) {
		try {
			errorSvc.markUnresolvedInAutonomousTx(event, exception);
			return onSuccess;
		} catch (Exception me) {
			log.fatalv(me, "Не удалось пометить событие {0} как требующее обработки", event);
			return EXECUTION_FAILED;
		}
	}

	private ExecutionStatus safeMarkPoisoned(QueueEventImpl event, Throwable exception, ExecutionStatus onSuccess) {
		try {
			errorSvc.markPoisonedInAutonomousTx(event, exception);
			return onSuccess;
		} catch (Exception me) {
			log.fatalv(me, "Не удалось пометить событие {0} как отравляющее для очереди", event);
			return EXECUTION_FAILED;
		}
	}

	private ExecutionStatus safeLogError(QueueEventImpl event, Throwable exception, ExecutionStatus onSuccess) {
		try {
			historySvc.errorInAutonomousTx("EXECUTION_FAILED", event, exception);
			return onSuccess;
		} catch (Exception me) {
			log.fatalv(me, "Не удалось сохранить в логе запись о провале обработки события {0}", event);
			return EXECUTION_FAILED;
		}
	}

	private static class ExecutionContex {
		private QueueEventImpl event;
		private ExecutionType executionType = ExecutionType.SERVICE;

		public QueueEventImpl getEvent() {
			return event;
		}

		public void setEvent(QueueEventImpl event) {
			this.event = event;
		}

		public ExecutionType getExecutionType() {
			return executionType;
		}

		public void setExecutionType(ExecutionType executionType) {
			this.executionType = executionType;
		}
	}

	private static enum ExecutionType {
		SERVICE, WORK_HANDLING, ERROR_HANDLING, POISON_HANDLING
	}

	//@formatter:off
	private static final long[] DELAY_MILLIS = {
		  10000L    /* 10 секунд */
		, 60000L    /* 1  минута */
		, 300000L   /* 5  минут  */
		, 1800000L  /* 30 минут  */
		, 18000000L /* 5  часов  */
	};//@formatter:on

	private static final long DEFAULT_DELAY_MILLIS = 86400000L; // 24 часа

	private static final int MAX_ATTEMPS_COUNT = 5;

	private static final Logger log = Logger.getLogger(QueueTaskTransactionalDelegate.class);
}
