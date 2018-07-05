package ru.argustelecom.box.env.billing.invoice;

import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceEntry;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class UsageInvoiceEntryDtoTranslator implements DefaultDtoTranslator<UsageInvoiceEntryDto, UsageInvoiceEntry> {

	@Override
	public UsageInvoiceEntryDto translate(UsageInvoiceEntry invoice) {
		return UsageInvoiceEntryDto.builder()
				.id(invoice.getId())
				.entries(invoice.getUsageInvoiceEntryContainer().getEntries())
				.build();
	}

}