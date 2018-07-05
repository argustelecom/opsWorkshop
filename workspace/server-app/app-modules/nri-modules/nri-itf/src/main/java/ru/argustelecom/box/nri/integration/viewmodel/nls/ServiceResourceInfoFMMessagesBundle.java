package ru.argustelecom.box.nri.integration.viewmodel.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface ServiceResourceInfoFMMessagesBundle {
	/**
	 * Возвращает Найдены бронирования по одной услуге для разных схем подключения
	 * @return
	 */
	@Message("Найдены бронирования по одной услуге для разных схем подключения")
	String foundBookingsByOneServiceForDifferentSchemas();
}
