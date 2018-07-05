package ru.argustelecom.box.env.billing.invoice;

import javax.inject.Inject;

import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.pricing.model.MeasuredProductOffering;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class MeasuredProductOfferingDtoTranslator implements DefaultDtoTranslator<MeasuredProductOfferingDto, MeasuredProductOffering> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public MeasuredProductOfferingDto translate(MeasuredProductOffering measuredProductOffering) {
		return MeasuredProductOfferingDto.builder()
				.id(measuredProductOffering.getId())
				.price(measuredProductOffering.getPrice())
				.objectName(measuredProductOffering.getObjectName())
				.pricelist(businessObjectDtoTr.translate(measuredProductOffering.getPricelist()))
				.build();
	}
}