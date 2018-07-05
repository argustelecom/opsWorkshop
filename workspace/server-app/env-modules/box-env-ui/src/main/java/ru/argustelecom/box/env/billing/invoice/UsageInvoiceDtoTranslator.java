package ru.argustelecom.box.env.billing.invoice;


import javax.inject.Inject;

import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class UsageInvoiceDtoTranslator implements DefaultDtoTranslator<UsageInvoiceDto, UsageInvoice> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public UsageInvoiceDto translate(UsageInvoice invoice) {
		//@formatter:off
		return UsageInvoiceDto.builder()
					.id(invoice.getId())
					.price(invoice.getTotalPrice())
					.startDate(invoice.getStartDate())
					.endDate(invoice.getEndDate())
					.closingDate(invoice.getClosingDate())
					.state(invoice.getState())
					.optionName(invoice.getOption().getObjectName())
					.service(businessObjectDtoTr.translate(invoice.getService()))
					.provider(businessObjectDtoTr.translate(invoice.getProvider()))
				.build();
		//@formatter:on
	}

}