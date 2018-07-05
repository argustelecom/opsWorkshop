package ru.argustelecom.box.inf.login;

import static java.util.Arrays.asList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import ru.argustelecom.box.inf.login.nls.LoginModuleResources;
import ru.argustelecom.system.inf.chrono.TZ;
import ru.argustelecom.system.inf.chrono.UnknownTimeZoneIDException;
import ru.argustelecom.system.inf.login.ArgusPrincipal;
import ru.argustelecom.system.inf.login.HomeRegion;

public class PersonalAreaLoginModule extends AbstractLoginModule {

	private Long uid;
	private PasswordEncrypt storedPassword;
	private Long customerId;
	private TimeZone tz;
	private Locale locale;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {

		super.initialize(subject, callbackHandler, sharedState, options);
		// TODO здесь нужно будет проинициализировать значение секретного ключа расшифровки пароля из jboss vault
	}

	@Override
	protected void loadLoginInfo(Connection connection) throws SQLException, LoginException {
		try (PreparedStatement stmt = connection.prepareStatement(PaLoginInfoQuery.SQL)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String encryptedPassword = PaLoginInfoQuery.password(rs);
					if (encryptedPassword == null) {
						throw LoginModuleResources.exception.brokenLoginInfo();
					}

					uid = PaLoginInfoQuery.uid(rs);
					storedPassword = new PasswordEncrypt(true, encryptedPassword);
					customerId = PaLoginInfoQuery.customerId(rs);

					String zoneId = PaLoginInfoQuery.timeZone(rs);
					try {
						tz = TZ.getTimeZone(zoneId);
					} catch (UnknownTimeZoneIDException e) {
						throw LoginModuleResources.exception.unknownTimeZone(e);
					}

					String localeId = PaLoginInfoQuery.locale(rs);
					if (localeId != null && !"".equals(localeId)) {
						locale = Locale.forLanguageTag(localeId);
					}
					if (locale == null) {
						locale = Locale.getDefault();
					}
				} else {
					throw LoginModuleResources.exception.credentialInvaid();
				}
			}
		}

	}

	@Override
	protected void authenticateUser(Connection connection) throws SQLException, LoginException {
		PasswordEncrypt typedPassword = new PasswordEncrypt(false, password);
		if (!Objects.equals(typedPassword, storedPassword)) {
			throw LoginModuleResources.exception.credentialInvaid();
		}
	}

	@Override
	protected ArgusPrincipal createUserCredentials() {
		HomeRegion dummy = new HomeRegion(-1L, DUMMY, null);
		ArgusPrincipal p = new ArgusPrincipal(uid, customerId, DUMMY, dummy, username, permissions, asList(dummy), tz,
				new Properties());
		p.setLocale(locale);
		return p;
	}

	@Override
	protected void loadPermissions(Connection connection) throws SQLException {
		super.loadPermissions(connection);
		permissions.put("Customer", "Клиент");
	}

	static final class PaLoginInfoQuery {
		//@formatter:off
		static final String SQL
			= "SELECT "
			+ "  uid, "
			+ "  password, "
			+ "  customer_id, "
			+ "  time_zone, "
			+ "  locale "
			+ "FROM system.pa_login "
			+ "WHERE username = ?"; //$NON-NLS-1$
		//@formatter:on

		public static Long uid(ResultSet rs) throws SQLException {
			return rs.getLong("uid"); //$NON-NLS-1$
		}

		static String password(ResultSet rs) throws SQLException {
			return rs.getString("password"); //$NON-NLS-1$
		}

		static Long customerId(ResultSet rs) throws SQLException {
			return rs.getLong("customer_id"); //$NON-NLS-1$
		}

		static String timeZone(ResultSet rs) throws SQLException {
			return rs.getString("time_zone"); //$NON-NLS-1$
		}

		static String locale(ResultSet rs) throws SQLException {
			return rs.getString("locale"); //$NON-NLS-1$
		}

		private PaLoginInfoQuery() {
		}
	}

	private static final long serialVersionUID = -77636022884771292L;
}
