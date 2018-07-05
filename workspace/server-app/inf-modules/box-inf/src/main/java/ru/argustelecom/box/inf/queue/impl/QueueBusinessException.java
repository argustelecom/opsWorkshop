package ru.argustelecom.box.inf.queue.impl;

public class QueueBusinessException extends Exception {

	private static final long serialVersionUID = -7295606753343312727L;

	public QueueBusinessException() {
		super();
	}

	public QueueBusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueueBusinessException(String message) {
		super(message);
	}

	public QueueBusinessException(Throwable cause) {
		super(cause);
	}
}
