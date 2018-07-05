package ru.argustelecom.box.env.saldo.imp.model;

public class RegisterException extends Exception {

	private static final long serialVersionUID = -139421012995935449L;

	public RegisterException() {
	}

	public RegisterException(String message) {
		super(message);
	}

	public RegisterException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegisterException(Throwable cause) {
		super(cause);
	}

	public RegisterException(String message, Throwable cause, boolean enableSuppression,
							 boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}