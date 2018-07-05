package ru.argustelecom.box.env.billing.invoice;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.invoice.model.RegularInvoice;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class RegularInvoiceDtoTranslator implements DefaultDtoTranslator<RegularInvoiceDto, RegularInvoice> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public RegularInvoiceDto translate(RegularInvoice invoice) {
		return RegularInvoiceDto.builder()
				.id(invoice.getId())
				.state(invoice.getState())
				.subscriptionName(invoice instanceof LongTermInvoice ? ((LongTermInvoice) invoice).getSubscription()
						.getObjectName() : ((UsageInvoice) invoice).getService().getObjectName())
				.totalPrice(invoice.getTotalPrice())
				.startDate(invoice.getStartDate())
				.endDate(invoice.getEndDate())
				.privilege(invoice instanceof LongTermInvoice && ((LongTermInvoice) invoice).getPrivilege() != null
						? businessObjectDtoTr.translate(((LongTermInvoice) invoice).getPrivilege()) : null)
				.build();
	}

}