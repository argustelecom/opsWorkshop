package ru.argustelecom.box.publang.base.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.modelbase.NamedObject;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = IEntity.TYPE_NAME)
public abstract class IEntity implements Serializable, Identifiable, NamedObject {

	public static final String TYPE_NAME = "iEntity";

	private Long id;
	private String objectName;

	@XmlAttribute(required = true)
	@Override
	public Long getId() {
		return id;
	}

	@XmlAttribute
	@Override
	public String getObjectName() {
		return objectName;
	}

	@Override
	public String toString() {
		return String.format("%s[id: %d; objectName: '%s']", getClass().getSimpleName(), id,
				objectName != null ? objectName : "-");
	}

	private static final long serialVersionUID = 5119042702967941911L;

}