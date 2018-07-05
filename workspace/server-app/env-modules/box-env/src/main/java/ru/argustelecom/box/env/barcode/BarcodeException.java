package ru.argustelecom.box.env.barcode;

public class BarcodeException extends RuntimeException {

	private static final long serialVersionUID = 8614474994637711307L;

	public BarcodeException(String message) {
		super(message);
	}

	public BarcodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public BarcodeException(Throwable cause) {
		super(cause);
	}

	public BarcodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}