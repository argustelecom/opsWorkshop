package ru.argustelecom.box.nri.booking.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации для BookingOrderRepository
 */
@MessageBundle(projectCode = "")
public interface BookingOrderRepositoryMessagesBundle {

	/**
	 * Возвращает строку Не удалось найти ресурс с указанным идентификатором
	 * @return
	 */
	@Message("Не удалось найти ресурс с указанным идентификатором ")
	String didNotFindResourceWithId();
}
