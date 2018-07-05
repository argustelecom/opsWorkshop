package ru.argustelecom.box.env.document.type;

import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class BillAnalyticTypeDtoTranslator implements DefaultDtoTranslator<BillAnalyticTypeDto, BillAnalyticType> {
	@Override
	public BillAnalyticTypeDto translate(BillAnalyticType billAnalyticType) {
		return new BillAnalyticTypeDto(billAnalyticType.getId(), billAnalyticType.getName(),
				billAnalyticType.getDescription(), billAnalyticType.getAnalyticCategory(),
				billAnalyticType.getAvailableForCustomPeriod());
	}
}