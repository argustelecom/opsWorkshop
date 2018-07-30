package ru.argustelecom.box.inf.login.nls;

import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.annotations.Message.Format.MESSAGE_FORMAT;

import java.util.Date;

import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface LoginModuleException {

	@LogMessage(level = ERROR)
	@Message(format = MESSAGE_FORMAT, value = "Системная ошибка, вход в систему невозможен. {0}")
	LoginException unknownLoginException(String causeMessage, @Cause Exception cause);

	@Message("Отсутствуют данные аутентификации. Обратитесь к администратору.")
	LoginException brokenLoginInfo();

	@Message("Имя учётной записи не указано. Вход в систему невозможен.")
	FailedLoginException usernameUnspecified();

	@Message("Пароль не указан. Вход в систему невозможен.")
	FailedLoginException passwordUnspecified();

	@Message("Неверный логин или пароль. Проверьте правильность введенных данных.")
	FailedLoginException credentialInvaid();

	@Message(format = MESSAGE_FORMAT, value = "Срок действия Вашего пароля истёк {0,date,long}. Укажите новый пароль для входа в систему.")
	CredentialExpiredException credentialExpired(Date from);

	@Message("Ваша учётная запись заблокирована. Вход в систему невозможен.")
	FailedLoginException accountLocked();

	@Message(value = "Не удалось определить часовой пояс пользователя. Вход в систему невозможен.")
	FailedLoginException unknownTimeZone(@Cause Exception cause);
}