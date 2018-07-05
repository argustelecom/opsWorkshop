package ru.argustelecom.box.nri.ports.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * Назначение порта
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PortPurpose  implements NamedObject {

	/**
	 * Абонентский порт
	 */
	SUBSCRIBER("{PortPurposeBundle:box.nri.ports.purpose.subscriber}"),

	/**
	 * Технологийсекий порт
	 */
	TECHNOLOGICAL("{PortPurposeBundle:box.nri.ports.purpose.technological}");

	private String name;

	public String getName() {
		return LocaleUtils.getLocalizedMessage(name, getClass());
	}

	public String getObjectName() {
		return getName();
	}
}
