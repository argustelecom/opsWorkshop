package ru.argustelecom.box.inf.security;

import static ru.argustelecom.box.inf.nls.LocaleUtils.format;

import ru.argustelecom.system.inf.exception.BusinessException;

public class PermissionNotGrantedException extends BusinessException {

	private static final long serialVersionUID = -7242316482544965026L;

	public PermissionNotGrantedException(String permissionId) {
		super(format("Для выполнения действия необходимо разрешение \"{0}\". Обратитесь к системному администратору.",
				permissionId));
	}

	public PermissionNotGrantedException(String action, String permissionId) {
		super(format(
				"Для выполнения действия \"{0}\" необходимо разрешение \"{1}\". Обратитесь к системному администратору.",
				action, permissionId));
	}

}
