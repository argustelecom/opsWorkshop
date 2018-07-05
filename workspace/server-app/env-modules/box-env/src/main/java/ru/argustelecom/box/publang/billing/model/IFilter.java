package ru.argustelecom.box.publang.billing.model;

import java.io.Serializable;
import java.util.Date;

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
@XmlType(name = IFilter.TYPE_NAME)
public class IFilter implements Serializable {

	private static final long serialVersionUID = -7158851853056772813L;

	public static final String TYPE_NAME = "iFilter";

	@XmlElement
	private Date dateFrom;

	@XmlElement
	private Date dateTo;

	@XmlElement
	private Long tariffId;

	@XmlElement
	private Long serviceId;

	@XmlElement
	private String processingStage;
}
