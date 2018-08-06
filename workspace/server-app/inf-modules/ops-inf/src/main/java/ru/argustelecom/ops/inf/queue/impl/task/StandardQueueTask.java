package ru.argustelecom.ops.inf.queue.impl.task;

import org.jboss.logging.Logger;

import ru.argustelecom.ops.inf.queue.impl.transactional.ExecutionStatus;

public class StandardQueueTask extends AbstractQueueTask {

	@Override
	public void run() {
		log.infov("Запускается стандартный поток обработки очереди событий {0}", getName());
		try {
			boolean interrupted;
			ExecutionStatus executionStatus;

			do {
				// пытаемся пингануть очередь и выполнить событие
				executionStatus = tryExecuteEvent();

				if (executionStatus.isFinalState()) {
					log.infov("Стандартный поток {0} приостанавливает работу. Статус: {1}", getName(), executionStatus);
				} else {
					log.debugv("Стандартный поток {0} обработал событие. Статус: {1}", getName(), executionStatus);
				}

				// Поток должен быть лоялен к прерыванию. Пока мы работали, нас могли прервать. Поэтому мы должны каждый
				// цикл выполнять проверку на прерывание.
				interrupted = Thread.currentThread().isInterrupted();

			} while (isActive() && !interrupted && !executionStatus.isFinalState());

			if (!isActive()) {
				log.infov("Получен сигнал остановки стандартного потока {0}", getName());
			}
			if (interrupted) {
				log.infov("Получен сигнал прерывания стандартного потока {0}", getName());
			}

		} finally {
			// Этот поток не может быть прерван при помощи исключения InterruptedException, т.к. не использует
			// блокирующих методов. Если же блокирующий метод будет использован кем-то внутри обработки задания,
			// то исключение InterruptedException завраппится в TransactionalDelegate (для провоцирования отката
			// транзакции) и погасится в супер-классе. Т.к. поток станет interrupted, то новый виток обработки не будет
			// запущен, поток прекратит работу и мы попадем в этот блок для снятия флага interrupted
			if (Thread.currentThread().isInterrupted()) {
				Thread.interrupted();
			}
		}
	}

	private static final Logger log = Logger.getLogger(StandardQueueTask.class);

}
