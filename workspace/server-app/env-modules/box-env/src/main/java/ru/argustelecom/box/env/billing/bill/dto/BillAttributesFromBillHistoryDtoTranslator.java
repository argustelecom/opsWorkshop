package ru.argustelecom.box.env.billing.bill.dto;

import ru.argustelecom.box.env.billing.bill.model.BillHistoryItem;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

import javax.inject.Inject;

@DtoTranslator
public class BillAttributesFromBillHistoryDtoTranslator implements DefaultDtoTranslator<BillAttributesDto,BillHistoryItem> {

	@Inject
	private BillAttributesDtoTranslator billAttributesDtoTranslator;

	@Override
	public BillAttributesDto translate(BillHistoryItem billHistoryItem) {
		BillAttributesDto billAttributesDto = billAttributesDtoTranslator.translate(billHistoryItem.getBill());
		billAttributesDto.setNumber(billHistoryItem.getNumber());
		billAttributesDto.setBillDate(billHistoryItem.getBillDate());
		return billAttributesDto;
	}
}
