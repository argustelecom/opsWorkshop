package ru.argustelecom.box.env.billing.bill;

import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class BillTypeDtoTranslator implements DefaultDtoTranslator<BillTypeDto, BillType> {

	@Override
	public BillTypeDto translate(BillType billType) {
		return new BillTypeDto(billType.getId(), billType.getObjectName());
	}

}