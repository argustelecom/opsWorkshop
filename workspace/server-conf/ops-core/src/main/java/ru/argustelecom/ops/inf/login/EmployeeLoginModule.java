package ru.argustelecom.ops.inf.login;

import static java.util.Arrays.asList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import ru.argustelecom.ops.inf.login.nls.LoginModuleResources;
import ru.argustelecom.system.inf.chrono.TZ;
import ru.argustelecom.system.inf.chrono.UnknownTimeZoneIDException;
import ru.argustelecom.system.inf.login.ArgusPrincipal;
import ru.argustelecom.system.inf.login.HomeRegion;

public class EmployeeLoginModule extends AbstractLoginModule {

	private PasswordHash.Algorithm hashAlgorithm;

	private Long uid;
	private PasswordHash storedPassword;
	private Date lockDate;
	private Date expiryDate;
	private Long employeeId;
	private TimeZone tz;
	private Locale locale;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {

		super.initialize(subject, callbackHandler, sharedState, options);

		this.hashAlgorithm = PasswordHash.Algorithm.get((String) options.get(HASH_ALGORITHM));
		if (this.hashAlgorithm == null) {
			this.hashAlgorithm = PasswordHash.Algorithm.SHA512;
			LoginModuleResources.logger.hashAlgorithmUnspecified();
		}
	}

	@Override
	protected void loadLoginInfo(Connection connection) throws SQLException, LoginException {
		try (PreparedStatement stmt = connection.prepareStatement(LoginInfoQuery.SQL)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String storedHash = LoginInfoQuery.password(rs);
					String storedSalt = LoginInfoQuery.salt(rs);

					if (storedHash == null || storedSalt == null) {
						throw LoginModuleResources.exception.brokenLoginInfo();
					}

					uid = LoginInfoQuery.uid(rs);
					storedPassword = new PasswordHash(storedHash, PasswordHash.saltFromString(storedSalt));
					lockDate = LoginInfoQuery.lockDate(rs);
					expiryDate = LoginInfoQuery.expiryDate(rs);
					employeeId = LoginInfoQuery.employeeId(rs);

					String zoneId = LoginInfoQuery.timeZone(rs);
					try {
						tz = TZ.getTimeZone(zoneId);
					} catch (UnknownTimeZoneIDException e) {
						throw LoginModuleResources.exception.unknownTimeZone(e);
					}

					String localeId = LoginInfoQuery.locale(rs);
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
	protected void loadPermissions(Connection connection) throws SQLException {
		super.loadPermissions(connection);

		permissions.put("User", "Пользователь");
		try (PreparedStatement stmt = connection.prepareStatement(PermissionsQuery.SQL)) {
			stmt.setLong(1, employeeId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String permissionId = PermissionsQuery.permissionId(rs);
					String permissionName = PermissionsQuery.permissionName(rs);
					permissions.put(permissionId, permissionName);
				}
			}
		}
	}

	@Override
	protected void authenticateUser(Connection connection) throws SQLException, LoginException {
		PasswordHash typedPassword = new PasswordHash(password, storedPassword.getSalt(), hashAlgorithm);
		Date now = new Date();

		if (!Objects.equals(typedPassword, storedPassword)) {
			throw LoginModuleResources.exception.credentialInvaid();
		}

		if (lockDate != null && now.after(lockDate)) {
			throw LoginModuleResources.exception.accountLocked();
		}

		if (expiryDate != null && now.after(expiryDate)) {
			throw LoginModuleResources.exception.credentialExpired(expiryDate);
		}
	}

	@Override
	protected ArgusPrincipal createUserCredentials() {
		HomeRegion dummy = new HomeRegion(-1L, DUMMY, null);
		ArgusPrincipal p = new ArgusPrincipal(uid, employeeId, DUMMY, dummy, username, permissions, asList(dummy), tz,
				new Properties());
		p.setLocale(locale);
		return p;
	}

	@Override
	protected void cleanup(boolean cleanSubject) {
		super.cleanup(cleanSubject);

		uid = null;

		storedPassword = null;
		lockDate = null;
		expiryDate = null;
		employeeId = null;

		tz = null;
		locale = null;
	}

	static final class LoginInfoQuery {
		//@formatter:off
		static final String SQL
			= "SELECT "
			+ "  uid, "
			+ "  password, "
			+ "  salt, "
			+ "  lock_date, "
			+ "  expiry_date, "
			+ "  time_zone,"
			+ "  locale,"
			+ "  employee_id "
			+ "FROM system.login "
			+ "WHERE username = ?"; //$NON-NLS-1$
		//@formatter:on

		public static Long uid(ResultSet rs) throws SQLException {
			return rs.getLong("uid"); //$NON-NLS-1$
		}

		static String password(ResultSet rs) throws SQLException {
			return rs.getString("password"); //$NON-NLS-1$
		}

		static String salt(ResultSet rs) throws SQLException {
			return rs.getString("salt"); //$NON-NLS-1$
		}

		static Date lockDate(ResultSet rs) throws SQLException {
			return rs.getDate("lock_date"); //$NON-NLS-1$
		}

		static Date expiryDate(ResultSet rs) throws SQLException {
			return rs.getDate("expiry_date"); //$NON-NLS-1$
		}

		static Long employeeId(ResultSet rs) throws SQLException {
			return rs.getLong("employee_id"); //$NON-NLS-1$
		}

		static String timeZone(ResultSet rs) throws SQLException {
			return rs.getString("time_zone"); //$NON-NLS-1$
		}

		static String locale(ResultSet rs) throws SQLException {
			return rs.getString("locale"); //$NON-NLS-1$
		}

		private LoginInfoQuery() {
		}
	}

	static final class PermissionsQuery {
		//@formatter:off
		static final String SQL
			= "WITH RECURSIVE per AS ( "
			+ "  SELECT p.* "
			+ "  FROM system.employee e "
			+ "    JOIN system.employee_roles er ON e.id = er.employee_id "
			+ "    JOIN system.role r ON er.role_id = r.id "
			+ "    JOIN system.role_permissions rp ON r.id = rp.role_id "
			+ "    JOIN system.permission p ON p.id = rp.permission_id "
			+ "  WHERE e.id = ? "
			+ "    AND r.status != 'DISABLED' "
			+ "  UNION "
			+ "  SELECT p.* "
			+ "  FROM system.permission p "
			+ "    JOIN per "
			+ "      ON p.id = per.parent_id "
			+ ") "
			+ "SELECT "
			+ "  per.id, "
			+ "  per.parent_id, "
			+ "  per.name "
			+ "FROM per"; //$NON-NLS-1$
		//@formatter:on

		static String permissionId(ResultSet rs) throws SQLException {
			return rs.getString("id"); //$NON-NLS-1$
		}

		static String permissionParentId(ResultSet rs) throws SQLException {
			return rs.getString("parent_id"); //$NON-NLS-1$
		}

		static String permissionName(ResultSet rs) throws SQLException {
			return rs.getString("name"); //$NON-NLS-1$
		}

		private PermissionsQuery() {
		}
	}

	private static final String HASH_ALGORITHM = "hashAlgorithm"; //$NON-NLS-1$
	private static final long serialVersionUID = -1754584516764929187L;
}