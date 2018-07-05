package ru.argustelecom.box.publang.billing.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

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
@XmlType(name = IReport.TYPE_NAME)
public class IReport implements Serializable {

	private static final long serialVersionUID = -7158851853056772813L;

	public static final String TYPE_NAME = "iReport";

	@XmlElement
	private String iRechargeJob;

	@XmlElement
	private IResult result;

	@XmlElement
	private ISummary iSummary;
}
