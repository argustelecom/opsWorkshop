package ru.argustelecom.box.nri.logicalresources.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации для контроллера диалога создания нового пула
 */
@MessageBundle(projectCode = "")
public interface PhoneNumberCreationDialogModelMessagesBundle {


	/**
	 * Отдаёт Ошибка:
	 * @return
	 */
	@Message("Ошибка: ")
	String error();

	/**
	 * Возвращает Телефонный номер уже существует в системе
	 * @return
	 */
	@Message("Телефонный номер уже существует в системе")
	String phoneNumberIsAlreadyExist();

	/**
	 * Для предложения Имя не должно быть пустым
	 * @return
	 */
	@Message("Имя не должно быть пустым")
	String nameCanNotBeEmpty();
}
