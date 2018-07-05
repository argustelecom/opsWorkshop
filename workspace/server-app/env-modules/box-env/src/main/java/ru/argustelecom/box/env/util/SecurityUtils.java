package ru.argustelecom.box.env.util;

import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;
import ru.argustelecom.system.inf.configuration.packages.Packages;

public final class SecurityUtils {

	public static final Long INF_MODULE = 1L;
	public static final Long NRI_MODULE = 777L;

	private static final String CRM_NRI_INTEGRATION = "box.crm.integration.nri.enabled";

	private SecurityUtils() {
	}

	/**
	 * Проверяет, что интеграция с NRI включена. Обеспечивается наличием модуля NRI и установкой настройки
	 * <tt>box.crm.nri.integration.enabled</tt> в значение <tt>true</tt>
	 * 
	 * @return <tt>true</tt>, если интеграция включена
	 */
	public static boolean isNriIntegrationEnabled() {
		Object prop = ServerRuntimeProperties.instance().getProperties().get(CRM_NRI_INTEGRATION);
		String propValue = prop == null ? null : prop.toString();
		return Packages.instance().isPackageDeployed(NRI_MODULE) && Boolean.parseBoolean(propValue);
	}

}
