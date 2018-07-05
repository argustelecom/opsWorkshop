package ru.argustelecom.box.env.mediation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.mediation.model.ReleaseCode;
import ru.argustelecom.box.env.mediation.model.UnsuitableCall;
import ru.argustelecom.box.env.mediation.model.MediationError;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnsuitableCallDto extends ConvertibleDto {
	private Long id;
	private MediationError errorType;
	private String errorMsg;
	private Long callId;
	private String foreignId;
	private String exchange;
	private Date callDate;
	private Long duration;
	private PeriodUnit cdrUnit;
	private ReleaseCode releaseCode;
	private String callingNumber;
	private String outgoingChannel;
	private String outgoingTrunk;
	private String calledNumber;
	private String incomingChannel;
	private String incomingTrunk;
	private BusinessObjectDto<Service> service;
	private BusinessObjectDto<AbstractTariff> tariff;
	private Long outgoingProvider;
	private Long incomingProvider;
	private String callDirection;
	private String identifiedBy;
	private Date rawCallDate;
	private String rawDuration;


	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return UnsuitableCallDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return UnsuitableCall.class;
	}
}
