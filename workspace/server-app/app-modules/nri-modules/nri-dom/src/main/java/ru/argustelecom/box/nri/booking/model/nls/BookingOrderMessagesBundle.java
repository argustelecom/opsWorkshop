package ru.argustelecom.box.nri.booking.model.nls;


import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface BookingOrderMessagesBundle {

	/**
	 * возвращает строку Нельзя создавать наряд без указания бронируемых ресурсов
	 *
	 * @return
	 */
	@Message("Нельзя создавать наряд без указания бронируемых ресурсов")
	String unacceptableToCreateBookingOrderWithoutResources();

	/**
	 * возвращает строку Наряд на бронирование ресурсов:
	 *
	 * @return
	 */
	@Message("Наряд на бронирование ресурсов: ")
	String defaultOrderName();

}
