package ru.argustelecom.box.env.message.mail;

public class SendingMailException extends Exception {

	private static final long serialVersionUID = -263487195468066020L;

	public SendingMailException() {
		super();
	}

	public SendingMailException(String message, Throwable cause) {
		super(message, cause);
	}

	public SendingMailException(String message) {
		super(message);
	}

	public SendingMailException(Throwable cause) {
		super(cause);
	}
}
