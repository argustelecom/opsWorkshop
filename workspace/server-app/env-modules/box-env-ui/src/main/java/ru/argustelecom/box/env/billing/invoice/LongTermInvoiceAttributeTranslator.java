package ru.argustelecom.box.env.billing.invoice;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class LongTermInvoiceAttributeTranslator
		implements DefaultDtoTranslator<LongTermInvoiceAttributeDto, LongTermInvoice> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public LongTermInvoiceAttributeDto translate(LongTermInvoice invoice) {
		//@formatter:off
		return LongTermInvoiceAttributeDto.builder()
					.id(invoice.getId())
					.subscription(businessObjectDtoTr.translate(invoice.getSubscription()))
					.typeProduct(businessObjectDtoTr.translate(invoice.getSubscription().getSubject()))
					.state(invoice.getState())
					.startDate(invoice.getStartDate())
					.endDate(invoice.getEndDate())
					.closingDate(invoice.getClosingDate())
					.totalPrice(invoice.getTotalPrice())
					.periodUnit(invoice.getPlan().chargingPeriod().getType().getBaseUnit())
				.build();
		//@formatter:on
	}
}
