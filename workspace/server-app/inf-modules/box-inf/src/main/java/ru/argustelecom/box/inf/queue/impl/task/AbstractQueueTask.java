package ru.argustelecom.box.inf.queue.impl.task;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.ManagedTaskListener;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.inf.queue.impl.QueueManagerCallback;
import ru.argustelecom.box.inf.queue.impl.transactional.ExecutionStatus;
import ru.argustelecom.box.inf.queue.impl.transactional.QueueTaskTransactionalDelegate;

@Dependent
public abstract class AbstractQueueTask implements ManagedTask, Runnable {

	private QueueManagerCallback callback;
	private ManagedTaskListener listener;
	private Map<String, String> properties;
	private boolean attached;

	@Inject
	private QueueTaskTransactionalDelegate delegate;

	public void attach(QueueManagerCallback callback, ManagedTaskListener listener, Map<String, String> properties) {
		this.callback = checkNotNull(callback);
		this.listener = checkNotNull(listener);
		this.properties = new HashMap<>();
		if (properties != null) {
			this.properties.putAll(properties);
		}
		this.attached = true;
	}

	public QueueManagerCallback getCallback() {
		return callback;
	}

	@Override
	public Map<String, String> getExecutionProperties() {
		return properties;
	}

	@Override
	public ManagedTaskListener getManagedTaskListener() {
		return listener;
	}

	public final String getName() {
		return Thread.currentThread().getName();
	}

	public final boolean isAttached() {
		return attached;
	}

	public final void fork() {
		checkState(attached);
		callback.fork();
	}

	public final boolean isActive() {
		checkState(attached);
		return callback.isActive();
	}

	protected final ExecutionStatus tryExecuteEvent() {
		long startTime = System.nanoTime();

		log.debug(">> TRY_EXECUTE_EVENT");
		ExecutionStatus executionStatus = delegate.tryExecute(this);
		log.debugv("<< TRY_EXECUTE_EVENT: {0} ms", (System.nanoTime() - startTime) / 1000000);

		return executionStatus;
	}
	
	private static final Logger log = Logger.getLogger(AbstractQueueTask.class);
}
