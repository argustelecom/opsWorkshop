package ru.argustelecom.ops.inf.queue.impl.model;

import java.text.MessageFormat;
import java.util.Date;

import ru.argustelecom.ops.inf.queue.api.QueueProducer.Priority;
import ru.argustelecom.ops.inf.queue.api.model.Queue;
import ru.argustelecom.ops.inf.queue.api.model.QueueEvent;
import ru.argustelecom.ops.inf.queue.api.model.QueueStatus;

public class QueueImpl implements Queue {

	private String id;
	private String groupId;
	private Priority priority;
	private Date scheduledTime;
	private QueueStatus status;
	private QueueEventImpl currentEvent;
	private boolean dirty = false;

	public QueueImpl() {
	}

	@Override
	public String getId() {
		return id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setId(String id) {
		this.id = id;
		this.dirty = true;
	}

	@Override
	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
		this.dirty = true;
	}

	@Override
	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Date sheduledTime) {
		this.scheduledTime = sheduledTime;
		this.dirty = true;
	}

	@Override
	public QueueStatus getStatus() {
		return status;
	}

	public void setStatus(QueueStatus status) {
		this.status = status;
		this.dirty = true;
	}

	@Override
	public QueueEvent getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(QueueEventImpl currentEvent) {
		this.currentEvent = currentEvent;
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
		return MessageFormat.format("Queue[queueId:{0}; priority:{1}, status:{2}, scheduledTime:{3}]", id, priority,
				status, scheduledTime);
	}
}
