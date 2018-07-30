package ru.argustelecom.box.inf.queue.api.worker;

public enum QueueErrorResult {

	/**
	 * 
	 */
	FAIL_QUEUE,

	/**
	 * 
	 */
	REJECT_EVENT,

	/**
	 * 
	 */
	RETRY_IMMEDIATE,

	/**
	 * 
	 */
	RETRY_LATER,

	/**
	 * 
	 */
	PROCESS_MANUALLY,

	/**
	 * 
	 */
	SCHEDULED_NEW_EVENT;

}
