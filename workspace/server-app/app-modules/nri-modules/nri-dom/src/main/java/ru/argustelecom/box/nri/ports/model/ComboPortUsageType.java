package ru.argustelecom.box.nri.ports.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * Тип испольщования комбо-порта
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ComboPortUsageType implements NamedObject {

	ETHERNET_PORT("{ComboPortUsageTypeBundle:box.nri.ports.combo.combo_port_usage_type.ethernet}"),

	OPTIC_TRANS("{ComboPortUsageTypeBundle:box.nri.ports.combo.combo_port_usage_type.transceiver}"),

	NOT_SPECIFIED("{ComboPortUsageTypeBundle:box.nri.ports.combo.combo_port_usage_type.not_specified}");

	private String name;

	public String getName() {
		return LocaleUtils.getLocalizedMessage(name, getClass());
	}

	public String getObjectName() {
		return getName();
	}
}
