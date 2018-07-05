package ru.argustelecom.box.env.billing.bill;

import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class BillReportDtoTranslator {
	//@formatter:off
	public BillReportDto translate(Bill bill) {
		return BillReportDto.builder()
				.id(bill.getId())
				.billNumber(bill.getDocumentNumber())
				.billDate(bill.getDocumentDate())
				.customerName(bill.getCustomer().getObjectName())
				.build();
	}
	//@formatter:on
}
