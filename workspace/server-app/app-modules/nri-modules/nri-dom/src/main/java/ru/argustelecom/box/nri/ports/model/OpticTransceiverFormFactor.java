package ru.argustelecom.box.nri.ports.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.system.inf.modelbase.NamedObject;


/**
 * Форм-фактор оптического трансивера
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OpticTransceiverFormFactor  implements NamedObject {

	SFP("SFP"),

	SFP_PLUS("SFP+"),

	GBIC("GBIC"),

	XENPAK("XENPAK"),

	X2("X2"),

	XFP("XFP");

	private String name;
	public String getName() {
		return name;
	}

	public String getObjectName() {
		return getName();
	}
}
