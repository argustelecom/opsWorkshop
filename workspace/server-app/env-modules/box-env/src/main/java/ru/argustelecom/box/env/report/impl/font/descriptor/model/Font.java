package ru.argustelecom.box.env.report.impl.font.descriptor.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(of = { "name", "encoding", "embedded" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Font {

	@XmlElement(required = true)
	private String name;

	@XmlElement(required = true)
	private String encoding = "Identity-H";

	@XmlElement(required = true)
	private boolean embedded = true;

}
