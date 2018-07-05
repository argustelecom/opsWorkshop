package ru.argustelecom.box.inf.queue.impl;

public class QueueSystemException extends Exception {

	private static final long serialVersionUID = -4641600245320613109L;

	public QueueSystemException() {
		super();
	}

	public QueueSystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueueSystemException(String message) {
		super(message);
	}

	public QueueSystemException(Throwable cause) {
		super(cause);
	}
}
