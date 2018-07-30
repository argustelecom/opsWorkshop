package ru.argustelecom.ops.inf.login.nls;

import org.jboss.logging.Messages;

public final class LoginModuleResources {

	private LoginModuleResources() {
	}

	public static final LoginModuleLog logger = Messages.getBundle(LoginModuleLog.class);
	public static final LoginModuleException exception = Messages.getBundle(LoginModuleException.class);
}
