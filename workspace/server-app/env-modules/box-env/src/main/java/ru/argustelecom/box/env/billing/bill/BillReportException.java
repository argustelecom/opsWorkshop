package ru.argustelecom.box.env.billing.bill;

public class BillReportException extends Exception {

	private static final long serialVersionUID = -7257055737664587357L;

	public BillReportException() {
		super();
	}

	public BillReportException(String message, Throwable cause) {
		super(message, cause);
	}

	public BillReportException(String message) {
		super(message);
	}

	public BillReportException(Throwable cause) {
		super(cause);
	}

}
