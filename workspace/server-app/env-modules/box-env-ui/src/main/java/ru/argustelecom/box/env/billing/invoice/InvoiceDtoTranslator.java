package ru.argustelecom.box.env.billing.invoice;

import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class InvoiceDtoTranslator implements DefaultDtoTranslator<InvoiceDto, AbstractInvoice> {

	@Override
	public InvoiceDto translate(AbstractInvoice invoice) {
		return new InvoiceDto(invoice.getId(), invoice.getObjectName(), invoice.getState());
	}

}