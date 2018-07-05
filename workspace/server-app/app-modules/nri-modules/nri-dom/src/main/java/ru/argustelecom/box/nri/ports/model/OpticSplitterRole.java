package ru.argustelecom.box.nri.ports.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * Роль оптического сплиттера
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OpticSplitterRole implements NamedObject {

	/**
	 * Исходящиц
	 */
	INCOMING("{OpticSplitterRoleBundle:box.nri.ports.optic_splitter_role.incoming}"),

	/**
	 * Входящий
	 */
	OUTCOMING("{OpticSplitterRoleBundle:box.nri.ports.optic_splitter_role.outcoming}");

	private String name;
	public String getName() {
		return LocaleUtils.getLocalizedMessage(name, getClass());
	}

	public String getObjectName() {
		return getName();
	}
}
