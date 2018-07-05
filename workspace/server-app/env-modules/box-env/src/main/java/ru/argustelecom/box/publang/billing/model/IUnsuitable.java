package ru.argustelecom.box.publang.billing.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = IUnsuitable.TYPE_NAME)
public class IUnsuitable implements Serializable {

	private static final long serialVersionUID = -121055870725235974L;

	public static final String TYPE_NAME = "iUnsuitable";

	@XmlElement
	private String unsuitableStage;

	@XmlElement
	private Integer unsuitableQuantity;
}
