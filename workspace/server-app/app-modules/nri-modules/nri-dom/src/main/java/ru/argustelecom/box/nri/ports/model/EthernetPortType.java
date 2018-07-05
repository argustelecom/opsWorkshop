package ru.argustelecom.box.nri.ports.model;

import lombok.Getter;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * Тип порта Ethernet
 */
public enum EthernetPortType implements NamedObject {

	PORT_TYPE_10E("10E"),

	PORT_TYPE_100FE("100FE"),

	PORT_TYPE_1GE("1GE"),

	PORT_TYPE_10GE("10GE");


	/**
	 * Название
	 */
	@Getter
	private String name;

	EthernetPortType(String name) {
		this.name = name;
	}

	public String getObjectName() {
		return getName();
	}
}
