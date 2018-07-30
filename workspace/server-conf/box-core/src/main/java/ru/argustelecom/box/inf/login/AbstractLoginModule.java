package ru.argustelecom.box.inf.login;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.sql.DataSource;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

import ru.argustelecom.box.inf.login.nls.LoginModuleResources;
import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;
import ru.argustelecom.system.inf.login.ArgusPrincipal;
import ru.argustelecom.system.inf.login.ArgusRolePrincipal;

public abstract class AbstractLoginModule implements LoginModule, Serializable {

	protected Subject subject;
	protected CallbackHandler callbackHandler;

	protected String username;
	protected String password;

	protected Map<String, String> permissions;

	protected boolean loginSuccess = false;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
	}

	@Override
	public boolean login() throws LoginException {
		try {
			loginSuccess = false;
			initCredentials();

			try (Connection connection = createDatabaseConnection()) {
				loadLoginInfo(connection);
				authenticateUser(connection);
				authorizeUser(connection);

				LoginModuleResources.logger.userSuccessfullyLoggedIn(username);
				loginSuccess = true;
				return loginSuccess;
			}
		} catch (LoginException e) {
			if (e.getClass() == LoginException.class) {
				LoginModuleResources.logger.unknownLoginException(e);
			} else {
				LoginModuleResources.logger.knownLoginException(e.getMessage());
			}
			throw e;
		} catch (Exception e) {
			throw LoginModuleResources.exception.unknownLoginException(e.getMessage(), e);
		}
	}

	@Override
	public boolean commit() throws LoginException {
		if (loginSuccess) {
			subject.getPrincipals().add(createUserIdentity());
			subject.getPrincipals().add(createUserCredentials());
			subject.getPrincipals().add(createUserPermissions());
		}

		cleanup(false);
		return loginSuccess;
	}

	@Override
	public boolean abort() throws LoginException {
		cleanup(true);
		return loginSuccess;
	}

	@Override
	public boolean logout() throws LoginException {
		cleanup(true);
		return true;
	}

	private void initCredentials() throws LoginException, IOException, UnsupportedCallbackException {
		NameCallback nameCB = new NameCallback(USERNAME_CALLBACK_PROMPT);
		PasswordCallback passwordCB = new PasswordCallback(PASSWORD_CALLBACK_PROMPT, false);
		Callback[] callbacks = new Callback[] { nameCB, passwordCB };

		callbackHandler.handle(callbacks);

		username = nameCB.getName();
		if (username == null || username.isEmpty())
			throw LoginModuleResources.exception.usernameUnspecified();

		password = null;
		if (passwordCB.getPassword() != null) {
			password = String.valueOf(passwordCB.getPassword());
		}

		if (password == null || password.isEmpty()) {
			throw LoginModuleResources.exception.passwordUnspecified();
		}
	}

	private Connection createDatabaseConnection() throws SQLException, LoginException {
		DataSource ds = ServerRuntimeProperties.instance().lookupDatasource();
		return ds.getConnection();
	}

	protected abstract void loadLoginInfo(Connection connection) throws SQLException, LoginException;

	protected abstract void authenticateUser(Connection connection) throws SQLException, LoginException;

	protected void authorizeUser(Connection connection) throws SQLException, LoginException {
		loadPermissions(connection);
	}

	protected void loadPermissions(Connection connection) throws SQLException {
		permissions = new LinkedHashMap<>();
	}

	protected Principal createUserIdentity() {
		return new SimplePrincipal(username);
	}

	protected abstract ArgusPrincipal createUserCredentials();

	protected Principal createUserPermissions() {
		SimpleGroup group = new SimpleGroup(ROLES_GROUP_NAME);
		for (Entry<String, String> permission : permissions.entrySet()) {
			ArgusRolePrincipal rp = new ArgusRolePrincipal(permission.getKey(), permission.getValue());
			group.addMember(rp);
		}
		return group;
	}

	protected void cleanup(boolean cleanSubject) {
		// Требуется делать для logout, но удалять только свои subject
		if (subject != null && cleanSubject) {
			Iterator<Principal> it = subject.getPrincipals().iterator();
			while (it.hasNext()) {
				Principal pr = it.next();
				if (Objects.equals(pr.getClass(), ArgusPrincipal.class)
						|| Objects.equals(pr.getClass(), ArgusRolePrincipal.class)) {
					it.remove();
				}
			}
		}

		subject = null;
		callbackHandler = null;

		username = null;
		password = null;

		permissions = null;
	}

	protected static final String DUMMY = "dummy"; //$NON-NLS-1$

	private static final String USERNAME_CALLBACK_PROMPT = "Username"; //$NON-NLS-1$
	private static final String PASSWORD_CALLBACK_PROMPT = "Password"; //$NON-NLS-1$
	private static final String ROLES_GROUP_NAME = "Roles"; //$NON-NLS-1$
	private static final long serialVersionUID = -5560498236067098389L;
}
