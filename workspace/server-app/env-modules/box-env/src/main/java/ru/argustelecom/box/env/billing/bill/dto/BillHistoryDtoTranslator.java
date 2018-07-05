package ru.argustelecom.box.env.billing.bill.dto;

import ru.argustelecom.box.env.billing.bill.model.BillHistoryItem;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class BillHistoryDtoTranslator implements DefaultDtoTranslator<BillHistoryItemDto, BillHistoryItem> {

	@Override
	public BillHistoryItemDto translate(BillHistoryItem billHistoryItem) {
		//@formatter:off
		return BillHistoryItemDto.builder().
					id(billHistoryItem.getId())
					.changedDate(billHistoryItem.getChangedDate())
					.employeeName(billHistoryItem.getEmployee().getObjectName())
					.version(billHistoryItem.getVersion())
				.build();
		//@formatter:on
	}

}