package ru.argustelecom.ops.inf.queue.impl.transactional;

public enum ExecutionStatus {

	/**
	 * 
	 */
	EXECUTED_SUCCESSFULLY(false),

	/**
	 * 
	 */
	EXECUTED_WITH_ERRORS(false),

	/**
	 * 
	 */
	INVAIN(true),

	/**
	 * 
	 */
	EXECUTION_FAILED(true);

	private boolean finalState;

	private ExecutionStatus(boolean finalState) {
		this.finalState = finalState;
	}

	public boolean isFinalState() {
		return finalState;
	}

}
