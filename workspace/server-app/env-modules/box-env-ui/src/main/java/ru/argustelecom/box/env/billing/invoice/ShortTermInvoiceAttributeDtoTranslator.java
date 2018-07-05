package ru.argustelecom.box.env.billing.invoice;

import ru.argustelecom.box.env.billing.invoice.model.ShortTermInvoice;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ShortTermInvoiceAttributeDtoTranslator
		implements DefaultDtoTranslator<ShortTermInvoiceAttributeDto, ShortTermInvoice> {
	@Override
	public ShortTermInvoiceAttributeDto translate(ShortTermInvoice invoice) {
		//@formatter:off
		return ShortTermInvoiceAttributeDto.builder()
					.id(invoice.getId())
					.state(invoice.getState())
					.closingDate(invoice.getClosingDate())
					.totalPrice(invoice.getTotalPrice())
				.build();
		//@formatter:on
	}
}
