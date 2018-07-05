package ru.argustelecom.box.inf.queue.api.model;

import java.util.Date;

public interface QueueEventError {

	QueueEvent getEvent();

	boolean isResolved();

	boolean isPoison();

	int getAttemptsCount();

	Date getErrorTime();

	String getErrorClass();

	String getErrorText();
}
