package ru.argustelecom.ops.env.party.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface PersonnelMessagesBundle {

	// *****************************************************************************************************************
	// Appointment
	// *****************************************************************************************************************

	@Message("Должность создана")
	String appointmentCreated();

	@Message("Должность '%s' была успешно создана")
	String appointmentSuccessfullyCreated(String name);

	@Message(value = "Должность удалена")
	String appointmentRemoved();

	@Message(value = "Должность '%s' была успешно удалена")
	String appointmentSuccessfullyRemoved(String name);

	// Employee
	@Message("Уже есть пользователь: '%s' с табельным номером: '%s'")
	String employeeAlreadyExists(String employee, String number);

}
