package ru.argustelecom.box.inf.queue.api.worker;

import java.io.Serializable;

import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;

public interface QueueHandler extends Serializable {

	/**
	 * 
	 * @param event
	 */
	QueueHandlingResult handleWork(QueueEvent event) throws Exception;

	/**
	 * 
	 * @param event
	 * @param error
	 * 
	 * @return
	 */
	QueueErrorResult handleError(QueueEvent event, QueueEventError error) throws Exception;

}
