package ru.argustelecom.box.env.contactperson.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface ContactPersonMessagesBundle {

	@Message(value = "Создание контактного лица")
	String contactPersonCreation();

	@Message(value = "Редактирование контактного лица")
	String contactPersonEditing();
}
