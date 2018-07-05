package ru.argustelecom.box.env.billing.invoice;

import static java.util.Optional.ofNullable;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.CLOSED;

import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class LongTermInvoiceDtoTranslator implements DefaultDtoTranslator<LongTermInvoiceDto, LongTermInvoice> {

	@Override
	public LongTermInvoiceDto translate(LongTermInvoice invoice) {
		//@formatter:off
		return LongTermInvoiceDto.builder()
					.id(invoice.getId())
					.price(invoice.getTotalPrice())
					.startDate(invoice.getStartDate())
					.endDate(invoice.getEndDate())
					.state(invoice.getState())
					.privilegeId(ofNullable(invoice.getPrivilege()).map(Privilege::getId).orElse(null))
					.privilegeName(ofNullable(invoice.getPrivilege()).map(Privilege::getObjectName).orElse(null))
				.build();
		//@formatter:on
	}

}