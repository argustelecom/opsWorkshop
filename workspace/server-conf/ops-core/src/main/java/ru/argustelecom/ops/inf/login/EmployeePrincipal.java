package ru.argustelecom.ops.inf.login;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;

import ru.argustelecom.system.inf.login.ArgusPrincipal;

public class EmployeePrincipal implements Principal, Serializable {

	private ArgusPrincipal delegate;

	private EmployeePrincipal(ArgusPrincipal delegate) {
		this.delegate = delegate;
	}

	public Long getUid() {
		return delegate.getLoginId();
	}

	@Override
	public String getName() {
		return delegate.getLoginName();
	}

	public String getUsername() {
		return delegate.getLoginName();
	}

	public Long getEmployeeId() {
		return delegate.getWorkerId();
	}

	public TimeZone getTimeZone() {
		return delegate.getTimeZone();
	}

	public void setTimeZone(TimeZone timeZone) {
		delegate.setTimeZone(timeZone);
	}

	public Locale getLocale() {
		return delegate.getLocale();
	}

	public void setLocale(Locale locale) {
		delegate.setLocale(locale);
	}

	public Collection<String> getPermissionIds() {
		return Collections.unmodifiableCollection(delegate.getRoles());
	}

	public Collection<String> getPermissionNames() {
		return Collections.unmodifiableCollection(delegate.getRolesUserFriendlyNames());
	}

	public static EmployeePrincipal instance() {
		ArgusPrincipal delegate = ArgusPrincipal.instanceUnchecked();
		if (delegate != null) {
			return new EmployeePrincipal(delegate);
		}
		return null;
	}

	private static final long serialVersionUID = -3261451538575927142L;
}
