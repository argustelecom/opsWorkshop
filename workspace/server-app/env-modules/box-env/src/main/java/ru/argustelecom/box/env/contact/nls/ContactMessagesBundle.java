package ru.argustelecom.box.env.contact.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface ContactMessagesBundle {

	// *****************************************************************************************************************
	// Contact Type
	// *****************************************************************************************************************
	@Message("Адрес электронной почты")
	String contactTypeEmail();

	@Message("Телефонный номер")
	String contactTypePhone();

	@Message("Учетная запись Skype")
	String contactTypeSkype();

	@Message("Специальный")
	String contactTypeCustom();

	@Message(value = "Тип контакта создан")
	String contactTypeCreated();

	@Message(value = "Тип контакта '%s' был успешно создан")
	String contactTypeSuccessfullyCreated(String name);

	@Message(value = "Тип контакта удалён")
	String contactTypeRemoved();

	@Message(value = "Тип контакта '%s' был успешно удалён")
	String contactTypeSuccessfullyRemoved(String name);

	@Message(value = "Тип контактов %s уже существует")
	String contactTypeAlreadyExist(String name);
}
