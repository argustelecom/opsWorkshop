package ru.argustelecom.box.nri.logicalresources.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс для контроллера фрейма пулов телефонных номеров
 */
@MessageBundle(projectCode = "")
public interface PhoneNumberPoolFrameModelMessagesBundle {

	/**
	 * Возвращает выражение Имя не уникально
	 * @return
	 */
	@Message("Имя не уникально")
	String nameDoesNotUnique();


	/**
	 * Возвращает выражение Имя не должно быть пустым
	 * @return
	 */
	@Message("Имя не должно быть пустым")
	String nameCanNotBeEmpty();
}
