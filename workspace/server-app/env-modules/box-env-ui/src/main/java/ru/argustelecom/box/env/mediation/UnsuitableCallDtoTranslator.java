package ru.argustelecom.box.env.mediation;

import javax.inject.Inject;

import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.mediation.model.UnsuitableCall;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class UnsuitableCallDtoTranslator implements DefaultDtoTranslator<UnsuitableCallDto,UnsuitableCall> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public UnsuitableCallDto translate(UnsuitableCall unsuitableCall) {

		return UnsuitableCallDto.builder()
				.id(unsuitableCall.getId())
				.callId(unsuitableCall.getCallId())
				.errorType(unsuitableCall.getErrorType())
				.errorMsg(unsuitableCall.getErrorMsg())
				.callDate(unsuitableCall.getCallDate())
				.incomingChannel(unsuitableCall.getIncomingChannel())
				.exchange(unsuitableCall.getSource())
				.outgoingChannel(unsuitableCall.getOutgoingChannel())
				.callingNumber(unsuitableCall.getCallingNumber())
				.outgoingTrunk(unsuitableCall.getOutgoingTrunk())
				.calledNumber(unsuitableCall.getCalledNumber())
				.incomingTrunk(unsuitableCall.getIncomingTrunk())
				.incomingProvider(unsuitableCall.getIncomingSupplier())
				.outgoingProvider(unsuitableCall.getOutgoingSupplier())
				.duration(unsuitableCall.getDuration())
				.cdrUnit(unsuitableCall.getCdrUnit())
				.foreignId(unsuitableCall.getForeignId())
				.service(unsuitableCall.getService() != null ? businessObjectDtoTr.translate(unsuitableCall.getService()) : null)
				.tariff(unsuitableCall.getTariff() != null ? businessObjectDtoTr.translate(unsuitableCall.getTariff()) : null)
				.identifiedBy(unsuitableCall.getIdentifiedBy())
				.callDirection(unsuitableCall.getCallDirection())
				.releaseCode(unsuitableCall.getReleaseCode())
				.rawCallDate(unsuitableCall.getRawCallDate())
				.rawDuration(unsuitableCall.getRawDuration())
				.build();
	}
}
