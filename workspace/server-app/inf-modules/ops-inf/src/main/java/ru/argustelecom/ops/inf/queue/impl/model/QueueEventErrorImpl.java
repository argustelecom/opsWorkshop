package ru.argustelecom.ops.inf.queue.impl.model;

import java.text.MessageFormat;
import java.util.Date;

import ru.argustelecom.ops.inf.queue.api.model.QueueEvent;
import ru.argustelecom.ops.inf.queue.api.model.QueueEventError;

public class QueueEventErrorImpl implements QueueEventError {

	private QueueEvent event;
	private boolean resolved;
	private boolean poison;
	private int attemptsCount;
	private Date errorTime;
	private String errorClass;
	private String errorText;

	@Override
	public QueueEvent getEvent() {
		return event;
	}

	public void setEvent(QueueEvent event) {
		this.event = event;
	}

	@Override
	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	@Override
	public boolean isPoison() {
		return poison;
	}

	public void setPoison(boolean poison) {
		this.poison = poison;
	}

	@Override
	public int getAttemptsCount() {
		return attemptsCount;
	}

	public void setAttemptsCount(int attemptsCount) {
		this.attemptsCount = attemptsCount;
	}

	@Override
	public Date getErrorTime() {
		return errorTime;
	}

	public void setErrorTime(Date errorTime) {
		this.errorTime = errorTime;
	}

	@Override
	public String getErrorClass() {
		return errorClass;
	}

	public void setErrorClass(String errorClass) {
		this.errorClass = errorClass;
	}

	@Override
	public String getErrorText() {
		return errorText;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	@Override
	public String toString() {
		return MessageFormat.format("QueueEventError[eventId:{0}, resolved:{1}, poison:{2}, attempt:{3}, error:{4}]",
				event != null ? event.getId() : "-", resolved, poison, attemptsCount, errorClass);
	}

}
