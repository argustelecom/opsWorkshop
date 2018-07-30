package ru.argustelecom.ops.inf.security;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import ru.argustelecom.ops.inf.service.DomainService;
import ru.argustelecom.system.inf.login.ArgusPrincipal;

@DomainService
public class SecurityContext implements Serializable {
	private static final long serialVersionUID = 8256398119422518081L;

	public boolean granted(Serializable permissionId) {
		return granted(permissionId.toString());
	}

	public boolean granted(String permissionId) {
		ArgusPrincipal principal = ArgusPrincipal.instanceUnchecked();
		return principal != null ? principal.getRoles().contains(permissionId) : false;
	}

	public void checkGranted(String action, Serializable permissionId) {
		checkGranted(action, permissionId.toString());
	}

	public void checkGranted(String action, String permissionId) {
		if (!granted(permissionId)) {
			throw new PermissionNotGrantedException(action, permissionId);
		}
	}

	public void checkGranted(Serializable permissionId) {
		checkGranted(permissionId.toString());
	}

	public void checkGranted(String permissionId) {
		if (!granted(permissionId)) {
			throw new PermissionNotGrantedException(permissionId);
		}
	}

	public String describe(Serializable permissionId) {
		return describe(permissionId.toString());
	}

	public String describe(String permissionId) {
		ArgusPrincipal principal = ArgusPrincipal.instanceUnchecked();
		return principal != null ? principal.getRolesMap().get(permissionId) : StringUtils.EMPTY;
	}
}
