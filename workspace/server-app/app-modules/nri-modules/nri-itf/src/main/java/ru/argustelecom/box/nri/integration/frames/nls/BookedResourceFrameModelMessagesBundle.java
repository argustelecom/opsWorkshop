package ru.argustelecom.box.nri.integration.frames.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации для контроллера фрейма вывдоа информации о забронированных ресурсах
 */
@MessageBundle(projectCode = "")
public interface BookedResourceFrameModelMessagesBundle {

	/**
	 * возвращает слово Ошибка! на многих языках
	 * @return
	 */
	@Message("Ошибка!")
	String error();

	/**
	 * возвращает фразу Не удалось отменить бронирование  на многих языках
	 * @return
	 */
	@Message("Не удалось отменить бронирование")
	String couldNotCancelBooking();

}
