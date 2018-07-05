package ru.argustelecom.box.nri.logicalresources.phone.model.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface PhoneNumberEntityListenerMessagesBundle {
	/**
	 * возвращает строку Не передан телефонный номер
	 *
	 * @return
	 */
	@Message("Не передан телефонный номер")
	String phoneNumberIsEmpty();

	/**
	 * возвращает строку Имя телефонного номера не может быть пустым
	 *
	 * @return
	 */
	@Message("Имя телефонного номера не может быть пустым")
	String phoneNumberNameIsEmpty();

	/**
	 * возвращает строку Телефонный номер не содержит цифр
	 *
	 * @return
	 */
	@Message("Телефонный номер не содержит цифр")
	String phoneNumberWithoutNumbers();
}
