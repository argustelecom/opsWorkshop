package ru.argustelecom.box.nri.service.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;


/**
 * Интерфейс локализации для ServiceSpecificationRepository
 */
@MessageBundle(projectCode = "")
public interface ServiceSpecificationRepositoryMessagesBundle {
	/**
	 * Возвращает строку Объект с id
	 *
	 * @return
	 */
	@Message("Объект с id ")
	String objectWithId();

	/**
	 * Возвращает строку  не найден.
	 *
	 * @return
	 */
	@Message(" не найден.")
	String didNotFind();
}
