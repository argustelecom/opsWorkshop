package ru.argustelecom.box.inf.login.nls;

import static org.jboss.logging.Logger.Level.DEBUG;
import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.Logger.Level.INFO;
import static org.jboss.logging.annotations.Message.Format.MESSAGE_FORMAT;

import javax.security.auth.login.LoginException;

import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

import ru.argustelecom.box.inf.login.EmployeeLoginModule;

@MessageBundle(projectCode = "")
public interface LoginModuleLog {

	@LogMessage(level = DEBUG, loggingClass = EmployeeLoginModule.class)
	@Message("Unknown password hashing algorithm, the algorithm by default will be used (SHA-512)")
	String hashAlgorithmUnspecified();

	@LogMessage(level = INFO, loggingClass = EmployeeLoginModule.class)
	@Message(format = MESSAGE_FORMAT, value = "User '{0}' successfully logged in!")
	String userSuccessfullyLoggedIn(String username);

	@LogMessage(level = ERROR, loggingClass = EmployeeLoginModule.class)
	@Message("Unknown error on login.")
	LoginException unknownLoginException(@Cause LoginException cause);

	@LogMessage(level = INFO, loggingClass = EmployeeLoginModule.class)
	@Message(format = MESSAGE_FORMAT, value = "Known error on login. {0}")
	LoginException knownLoginException(String causeMessage);

}