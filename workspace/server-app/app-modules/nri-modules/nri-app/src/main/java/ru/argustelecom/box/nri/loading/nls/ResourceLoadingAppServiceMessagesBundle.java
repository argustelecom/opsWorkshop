package ru.argustelecom.box.nri.loading.nls;


import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface ResourceLoadingAppServiceMessagesBundle {

	/**
	 * Возвращает строку не указана бронь
	 *
	 * @return
	 */
	@Message("не указана бронь")
	String doNotSetBooking();
}
