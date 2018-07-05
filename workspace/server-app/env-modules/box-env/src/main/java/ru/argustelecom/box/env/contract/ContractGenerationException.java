package ru.argustelecom.box.env.contract;

public class ContractGenerationException extends Exception {

	private static final long serialVersionUID = -2516315980436013245L;

	public ContractGenerationException() {
		super();
	}

	public ContractGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContractGenerationException(String message) {
		super(message);
	}

	public ContractGenerationException(Throwable cause) {
		super(cause);
	}

}