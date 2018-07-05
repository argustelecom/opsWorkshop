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
@XmlType(name = IUnsuitableCall.TYPE_NAME)
public class IUnsuitableCall implements Serializable {

	private static final long serialVersionUID = 6735966106266948340L;

	public static final String TYPE_NAME = "iUnsuitableCall";

	@XmlElement(required = true)
	private Long callId;

	@XmlElement
	private Date callDate;

	@XmlElement
	private String callDirection;

	@XmlElement
	private Integer duration;

	@XmlElement
	private String cdrUnit;

	@XmlElement
	private String callingNumber;

	@XmlElement
	private String calledNumber;

	@XmlElement
	private String outgoingChannel;

	@XmlElement
	private String outgoingTrunk;

	@XmlElement
	private String incomingChannel;

	@XmlElement
	private String incomingTrunk;

	@XmlElement
	private String releaseCode;

	@XmlElement(required = true)
	private String processingStage;

}
