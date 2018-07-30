package ru.argustelecom.ops.inf.queue.impl.model;

import static java.text.MessageFormat.format;

import java.util.Date;

import ru.argustelecom.ops.inf.queue.api.context.Context;
import ru.argustelecom.ops.inf.queue.api.model.Queue;
import ru.argustelecom.ops.inf.queue.api.model.QueueEvent;
import ru.argustelecom.ops.inf.queue.api.model.QueueEventError;
import ru.argustelecom.ops.inf.queue.impl.context.ContextMapper;
import ru.argustelecom.system.inf.utils.ReflectionUtils;

public class QueueEventImpl implements QueueEvent {

	private Long id;
	private QueueImpl queue;
	private Date scheduledTime;
	private String handlerName;
	private String marshalledContext;
	private QueueEventErrorImpl lastError;
	private boolean dirty = false;

	public QueueEventImpl() {
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
		this.dirty = true;
	}

	@Override
	public Queue getQueue() {
		return queue;
	}

	public void setQueue(QueueImpl queue) {
		this.queue = queue;
		this.dirty = true;
	}

	@Override
	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
		this.dirty = true;
	}

	@Override
	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
		this.dirty = true;
	}

	@Override
	public <T extends Context> T getContext(Class<T> contextClass) {
		T context = null;
		if (contextClass != null && ContextMapper.isValid(marshalledContext)) {
			context = ReflectionUtils.newInstance(contextClass, this);
			ContextMapper.update(context, marshalledContext);
		}
		return context;
	}

	@Override
	public <T extends Context> void setContext(T context) {
		marshalledContext = context != null ? ContextMapper.marshall(context) : null;
		this.dirty = true;
	}

	@Override
	public QueueEventError getLastError() {
		return lastError;
	}

	public void setException(QueueEventErrorImpl lastError) {
		this.lastError = lastError;
		this.dirty = true;
	}

	public String getMarshalledContext() {
		return marshalledContext != null ? marshalledContext : ContextMapper.EMPTY_CONTEXT;
	}

	public void setMarshalledContext(String marshalledContext) {
		this.marshalledContext = marshalledContext;
		this.dirty = true;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void cleanDirtyState() {
		this.dirty = false;
	}

	@Override
	public String toString() {
		//@formatter:off
		return format("QueueEvent[queueId:{0}; queueStatus:{1}, queuePriority:{2}, eventId:{3}, handlerName:{4}]",
				queue != null ? queue.getId() : "-", 
				queue != null ? queue.getStatus() : "-",
				queue != null ? queue.getPriority() : "-", 
				id, handlerName
		);//@formatter:on
	}
}
