package ru.argustelecom.box.nri.ports.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;


/**
 * Интерфейс для локализации
 */
@MessageBundle(projectCode = "")
public interface PortAppServiceBundle {

	/**
	 * Возвращает строку Не смогли найти порт
	 * @return
	 */
	@Message("Не смогли найти порт")
	String didNotFindPort();

	/**
	 * Возвращает строку Порт null
	 * @return
	 */
	@Message("Порт null")
	String portIsNull();

	/**
	 * Возвращает строку Порт null
	 * @return
	 */
	@Message("id ресурса null")
	String resourceIdIsNull();

	/**
	 * Возвращает строку Порт null
	 * @return
	 */
	@Message("Не нашли ресурс")
	String didNotFindResource();

}
