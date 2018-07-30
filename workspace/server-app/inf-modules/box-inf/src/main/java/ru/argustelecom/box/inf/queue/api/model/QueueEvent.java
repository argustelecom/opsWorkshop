package ru.argustelecom.box.inf.queue.api.model;

import java.util.Date;

import ru.argustelecom.box.inf.queue.api.context.Context;

public interface QueueEvent {

	Long getId();

	Queue getQueue();

	String getHandlerName();

	Date getScheduledTime();

	QueueEventError getLastError();

	public <T extends Context> T getContext(Class<T> contextClass);

	public <T extends Context> void setContext(T context);
}
