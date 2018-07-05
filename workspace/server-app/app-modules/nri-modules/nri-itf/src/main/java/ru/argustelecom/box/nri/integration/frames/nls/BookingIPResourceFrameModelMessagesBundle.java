package ru.argustelecom.box.nri.integration.frames.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации для контроллера фрейма вывода информации о бронировании IP адреса
 */
@MessageBundle(projectCode = "")
public interface BookingIPResourceFrameModelMessagesBundle {

	/**
	 * возвращает слово Ошибка! на многих языках
	 * @return
	 */
	@Message("Ошибка!")
	String error();

	/**
	 * возвращает фразу Не удалось забронировать ресурс по правилу  на многих языках
	 * @return
	 */
	@Message("Не удалось забронировать ресурс по правилу ")
	String couldNotBookResourceByRule();
}
