package ru.argustelecom.ops.inf.queue.impl.task;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.logging.Logger;

import ru.argustelecom.ops.inf.queue.impl.QueueSystemException;
import ru.argustelecom.ops.inf.queue.impl.transactional.ExecutionStatus;
import ru.argustelecom.ops.inf.queue.impl.util.ServerStartExpectant;

public class DaemonQueueTask extends AbstractQueueTask {

	private AtomicLong cycle = new AtomicLong(0);

	@Override
	public void run() {

		log.infov("Запускается фоновый поток обработки очереди событий {0}", getName());
		try {
			// нас могли запустить до того, как сервер закончит загрузку поэтому необходимо подождать, пока сервер
			// успешно запустится. Если сервер запустится с ошибками, то метод бросит InterruptedException
			awaitServerRunning();

			// Пока не поступил явный сигнал от QueueManager будем периодически пинговать очередь на предмет появления
			// в ней событий
			while (isActive() && !Thread.currentThread().isInterrupted()) {
				log.infov("фоновый поток обработки очереди событий {0} выполняет проверку очереди #{1}", getName(),
						cycle.incrementAndGet());

				// Попытаемся получить событие и выполнить его...
				ExecutionStatus executionStatus = tryExecuteEvent();
				log.debugv("Фоновый поток {0} обработал событие. Статус: {1}", getName(), executionStatus);

				if (!executionStatus.isFinalState()) {
					// Поток должен быть лоялен к прерыванию. Пока мы работали, нас могли прервать. Поэтому мы должны
					// перед тем, как пытаться выполнить еще одно событие убедиться, что нам не отправили прерывание
					boolean interrupted = Thread.currentThread().isInterrupted();

					// Если событие получено и выполнено (не важно, с каким статусом, главное, что очередь не пуста)
					// то будем пытаться получить и обработать событие снова и снова. Естественно, если нас не попросили
					// прервать обработку
					while (isActive() && !interrupted && !executionStatus.isFinalState()) {
						executionStatus = tryExecuteEvent();
						log.debugv("Фоновый поток {0} обработал событие. Статус: {1}", getName(), executionStatus);

						interrupted = Thread.currentThread().isInterrupted();
					}
				}

				if (isActive()) {
					// Если мы получили прерывание, то вызов этого блокирующего метода тутже выбросит
					// InterruptedException
					log.infov("Фоновый поток {0} приостанавливает работу на {1} мин.", getName(), SLEEP_DURATION_MIN);
					TimeUnit.MINUTES.sleep(SLEEP_DURATION_MIN);
				}
			}

			log.infov("Получен сигнал остановки фонового потока {0}", getName());

		} catch (InterruptedException e) {

			// Сюда попадаем только из блокирующего метода Thread.sleep(). Никакой особой обработки не требуется, т.к.
			// мы не успели ничего сделать. Просто залоггируем факт прерывания и тихо умрем, вернув поток в пул
			log.warnv("Получен сигнал прерывания фонового потока {0}", getName());

		} catch (Exception e) {

			// Сюда попадаем если не смогли подождать запуска сервера, проверить его состояние или если вообще, все
			// сломалось
			log.fatalv(e, "Фоновоый поток обработки очереди {0} аварийно завершился", getName());

		} finally {

			// Мы должны гарантировать снятие флага прерывания для корректного возвращения потока в пул
			if (Thread.currentThread().isInterrupted()) {
				Thread.interrupted();
			}

		}
	}

	private void awaitServerRunning() throws QueueSystemException {
		boolean serverSuccesfullyRunning = false;

		try {
			serverSuccesfullyRunning = new ServerStartExpectant().awaitServerSuccessfullyRunningInCurrentThread();

			// Подождем дополнительно стандартный интервал. Т.к. для детекта старта сервера используется механизм,
			// аналогичный платформенному, то возможно получить ситуацию, при которой наш ожидатель отлип раньше
			// платформенного и, как следствие, очередь начала шуршать до того, как платформенный механизм выполнил
			// завершение инициализации сервера, что может привести к неожиданным и непредсказуемым последствиям (если
			// не сейчас, то возможно в будущем)
			TimeUnit.MINUTES.sleep(SLEEP_DURATION_MIN);
		} catch (Exception e) {
			throw new QueueSystemException("Ожидание запуска сервера аварийно завершилось", e);
		}

		if (!serverSuccesfullyRunning) {
			throw new QueueSystemException("Сервер запущен с ошибками или имеет неизвестное состояние");
		}
	}

	private static final long SLEEP_DURATION_MIN = 1;
	private static final Logger log = Logger.getLogger(DaemonQueueTask.class);
}
