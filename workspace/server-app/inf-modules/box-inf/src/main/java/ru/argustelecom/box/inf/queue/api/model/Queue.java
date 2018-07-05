package ru.argustelecom.box.inf.queue.api.model;

import java.util.Date;

import ru.argustelecom.box.inf.queue.api.QueueProducer.Priority;

public interface Queue {

	String getId();

	String getGroupId();

	Priority getPriority();

	QueueStatus getStatus();

	Date getScheduledTime();

	QueueEvent getCurrentEvent();

}
