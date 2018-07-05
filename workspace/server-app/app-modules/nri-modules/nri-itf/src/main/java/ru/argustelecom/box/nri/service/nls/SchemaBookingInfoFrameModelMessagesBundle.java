package ru.argustelecom.box.nri.service.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации для SchemaBookingInfoFrameModel
 */
@MessageBundle(projectCode = "")
public interface SchemaBookingInfoFrameModelMessagesBundle {

	/**
	 * отдаёт слово ошибка
	 * @return
	 */
	@Message("Ошибка.")
	String error();

	/**
	 * отдаёт фразу Не удалось удалить требование:
	 * @return
	 */
	@Message("Не удалось удалить требование: ")
	String couldNotDeleteRequirement();
}
