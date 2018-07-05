package ru.argustelecom.box.nri.ports.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * среда передачи
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TransmissionMedium  implements NamedObject {
	COPPER("{TransmissionMediumBundle:box.nri.ports.medium.copper}"),
	OPTIC("{TransmissionMediumBundle:box.nri.ports.medium.optic}"),
	NOT_CHOSEN("{TransmissionMediumBundle:box.nri.ports.medium.not_chosen}");

	/**
	 * Имя
	 */
	private String name;

	public String getName() {
		return LocaleUtils.getLocalizedMessage(name, getClass());
	}

	public String getObjectName() {
		return getName();
	}
}
