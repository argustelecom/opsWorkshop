package ru.argustelecom.box.env.billing.invoice.unsuitable;

import ru.argustelecom.box.env.billing.invoice.model.UnsuitableRatedOutgoingCallsView;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

import javax.inject.Inject;

@DtoTranslator
public class UnsuitableRatedOutgoingCallsViewDtoTranslator
		implements DefaultDtoTranslator<UnsuitableRatedOutgoingCallsViewDto, UnsuitableRatedOutgoingCallsView> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public UnsuitableRatedOutgoingCallsViewDto translate(UnsuitableRatedOutgoingCallsView call) {
		// @formatter:off
		return UnsuitableRatedOutgoingCallsViewDto.builder()
					.id(call.getId())
					.callDate(call.getCallDate())
					.duration(call.getDuration())
					.ratedUnit(call.getRatedUnit())
					.amount(call.getAmount())
					.tariff(businessObjectDtoTr.translate(call.getTariff()))
					.resourceNumber(call.getResourceNumber())
					.service(businessObjectDtoTr.translate(call.getService()))
					.supplier(businessObjectDtoTr.translate(call.getSupplier()))
					.zone(businessObjectDtoTr.translate(call.getZone()))
				.build();
		// @formatter:on
	}
}
