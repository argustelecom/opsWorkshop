package ru.argustelecom.box.nri.ports.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * Техническое состояние
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PortTechnicalCondition  implements NamedObject {

	IN_ORDER("{PortTechnicalConditionBundle:box.nri.ports.in_order}"),

	DEFECTIVE("{PortTechnicalConditionBundle:box.nri.ports.defective}");

	private String name;

	public String getName() {
		return LocaleUtils.getLocalizedMessage(name, getClass());
	}

	public String getObjectName() {
		return getName();
	}
}
