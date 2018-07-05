package ru.argustelecom.box.publang.billing.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = ISummary.TYPE_NAME)
public class ISummary implements Serializable {

	private static final long serialVersionUID = 5061820467746482982L;

	public static final String TYPE_NAME = "iSummary";

	@XmlElement
	private Integer totalProcessed;

	@XmlElement
	private Integer success;

	@XmlElement
	private List<IUnsuitable> unsuitable;
}
