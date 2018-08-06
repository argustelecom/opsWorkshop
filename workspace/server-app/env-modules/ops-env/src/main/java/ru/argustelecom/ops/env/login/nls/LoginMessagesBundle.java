package ru.argustelecom.ops.env.login.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface LoginMessagesBundle {

	@Message(value = "Невозможно создать учётную запись") 
	String cannotCreateLogin();
	
	@Message(value = "Неверное подтверждение пароля")
	String incorrectPasswordConfirmation();
	
	@Message(value = "Учётная запись создана")
	String loginCreated();
	
	@Message(value = "Учётная запись '%s', для пользователя '%s' успешно создана")
	String loginSuccessfullyCreated(String loginName, String employeeName);

	@Message("Логин %s не найден или не уникален")
	String loginNotFoundOrNotUnique(String login);

	@Message("Логин %s уже существует")
	String loginAlreadyExists(String login);

	@Message("Введенный пароль не совпадает с текущим действующим паролем")
	String confirmationDoesNotMatchPassword();
}
