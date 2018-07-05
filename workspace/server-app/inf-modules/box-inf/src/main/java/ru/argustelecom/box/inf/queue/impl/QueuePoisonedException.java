package ru.argustelecom.box.inf.queue.impl;

public class QueuePoisonedException extends Exception {

	private static final long serialVersionUID = 6431089412301056689L;

	public QueuePoisonedException() {
		super();
	}

	public QueuePoisonedException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueuePoisonedException(String message) {
		super(message);
	}

	public QueuePoisonedException(Throwable cause) {
		super(cause);
	}
}
