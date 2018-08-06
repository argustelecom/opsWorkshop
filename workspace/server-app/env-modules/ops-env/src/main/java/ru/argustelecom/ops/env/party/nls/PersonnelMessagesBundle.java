package ru.argustelecom.ops.env.party.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface PersonnelMessagesBundle {

	// Employee
	@Message("Уже есть пользователь: '%s' с табельным номером: '%s'")
	String employeeAlreadyExists(String employee, String number);

}
