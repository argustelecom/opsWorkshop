package ru.argustelecom.box.inf.queue.impl;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedTaskListener;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.inf.queue.api.QueueManager;
import ru.argustelecom.box.inf.queue.api.QueueManagerStatus;
import ru.argustelecom.box.inf.queue.impl.task.DaemonQueueTask;
import ru.argustelecom.box.inf.queue.impl.task.StandardQueueTask;
import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;

import com.google.common.base.Strings;

@Startup
@Singleton
@Lock(LockType.READ)
public class QueueManagerImpl implements QueueManager, QueueManagerCallback, ManagedTaskListener {

	private AtomicBoolean active = new AtomicBoolean(false);
	private AtomicBoolean daemonAttached = new AtomicBoolean(false);

	@Resource(lookup = "java:jboss/ee/concurrency/executor/QueueHandler")
	private ManagedExecutorService pool;

	@Inject
	private Instance<DaemonQueueTask> daemonTaskInstance;

	@Inject
	private Instance<StandardQueueTask> standardTaskInstance;

	@PostConstruct
	protected void postConstruct() {
		if (isAutoRunnable()) {
			log.info("Автоматический запуск обработчика очереди: box.queue.autorunnable == true");
			startup();
		}
	}

	@PreDestroy
	protected void preDestroy() {
		QueueManagerStatus status = getStatus();
		if (status != QueueManagerStatus.INACTIVE) {
			log.infov("Во время остановки обработчика очередь находится состоянии: {0}. Останавливаем...", status);
			shutdown();
			if (!awaitTermination(TimeUnit.SECONDS.toMillis(15))) {
				log.warn("Не удалось дождаться корректной остановки очереди");
			} else {
				log.info("Обработчик очереди успешно остановлен");
			}
		}
	}

	@Override
	@Lock(LockType.WRITE)
	public boolean awaitTermination(long timeout) {
		boolean result = false;
		if (getStatus() == QueueManagerStatus.DEACTIVATING && timeout > 0) {
			try {
				long duration = 0;
				long timeToSleep = Math.min(100, timeout);
				while (true) {
					duration += timeToSleep;
					if (duration >= timeout || (result = getStatus() == QueueManagerStatus.INACTIVE)) {
						break;
					}
					Thread.sleep(timeToSleep);
				}
			} catch (InterruptedException e) {
				log.error("Во время ожидания остановки очереди текущий поток был прерван", e);
				Thread.interrupted();
			}
		}
		return result;
	}

	@Override
	@Lock(LockType.WRITE)
	public boolean startup() {
		boolean result = false;
		if (!active.get()) {
			active.set(true);
			if (!daemonAttached.get()) {
				DaemonQueueTask daemon = daemonTaskInstance.get();
				daemon.attach(this, this, null);
				pool.submit(daemon);
			}
			result = true;

			log.info("Обработчик очереди успешно запущен");
		}
		return result;
	}

	@Override
	public boolean shutdown() {
		log.info("Активным потокам обработки очереди отправлен сигнал останова. Важно! Очередь еще не остановлена!");
		return active.getAndSet(false);
	}

	@Override
	public QueueManagerStatus getStatus() {
		boolean active = this.active.get();
		boolean daemonAttached = this.daemonAttached.get();

		if (!active && !daemonAttached) {
			return QueueManagerStatus.INACTIVE;
		}
		if (active && !daemonAttached) {
			return QueueManagerStatus.ACTIVATING;
		}
		if (active && daemonAttached) {
			return QueueManagerStatus.ACTIVE;
		}

		return QueueManagerStatus.DEACTIVATING;
	}

	@Override
	public boolean isActive() {
		return active.get();
	}

	@Override
	public boolean fork() {
		boolean result = false;
		if (active.get()) {

			StandardQueueTask task = standardTaskInstance.get();
			task.attach(this, this, null);
			try {
				pool.submit(task);
				result = true;
				log.debug("Успешно запущен новый стандартный таск обработки очереди");
			} catch (RejectedExecutionException e) {
				// Если не удалось запустить новый поток, то нужно сразу за собой почистить. Потому что в этом случае мы
				// не попадаем в жизненный цикл таска и не сможем удалить ненужный инстанс из taskDone
				safeDestroyTask(task);
				log.debug("Не удалось запустить стандартный таск обработки очереди, пул потоков заполнен");
			}
		}
		return result;
	}

	@Override
	public void taskAborted(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
		// судя по жизненному циклу, описанному в документации, следующее состояние абортированного таска - это done.
		// Т.е. чтобы подчистить инстансы, которые мы создали, необходимо и достаточно вызвать метод destroy из
		// обработчика taskDone
	}

	@Override
	public void taskDone(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
		if (task instanceof DaemonQueueTask) {
			daemonAttached.set(false);
		}
		safeDestroyAbstractTask(task);
	}

	@Override
	public void taskStarting(Future<?> future, ManagedExecutorService executor, Object task) {
		// на текущий момент нам не интересно отслеживать начало работы таска
	}

	@Override
	public void taskSubmitted(Future<?> future, ManagedExecutorService executor, Object task) {
		// демон может быть только один на очередь, поэтому если засобмиттили демона, то нужно сохранить это в
		// соответствующем флаге
		if (task instanceof DaemonQueueTask) {
			daemonAttached.set(true);
		}
	}

	private void safeDestroyAbstractTask(Object task) {
		if (task instanceof DaemonQueueTask) {
			safeDestroyTask((DaemonQueueTask) task);
		}
		if (task instanceof StandardQueueTask) {
			safeDestroyTask((StandardQueueTask) task);
		}
	}

	private void safeDestroyTask(DaemonQueueTask task) {
		log.debugv("safeDestroyTask: Удаляется таск {0}", task);
		try {
			daemonTaskInstance.destroy(task);
		} catch (UnsupportedOperationException e) {
			log.error("safeDestroyTask: Не удалось уничтожить DaemonQueueTask. Возможны утечки памяти", e);
		}
	}

	private void safeDestroyTask(StandardQueueTask task) {
		log.debugv("safeDestroyTask: Удаляется таск {0}", task);
		try {
			standardTaskInstance.destroy(task);
		} catch (UnsupportedOperationException e) {
			log.error("safeDestroyTask: Не удалось уничтожить StandardQueueTask. Возможны утечки памяти", e);
		}
	}

	private boolean isAutoRunnable() {
		Object prop = ServerRuntimeProperties.instance().getProperties().get(AUTO_RUNNABLE);
		String propValue = prop == null ? null : prop.toString();
		return !Strings.isNullOrEmpty(propValue) && Boolean.parseBoolean(propValue);
	}

	private static final String AUTO_RUNNABLE = "box.queue.autorunnable";

	private static final Logger log = Logger.getLogger(QueueManagerImpl.class);
}
