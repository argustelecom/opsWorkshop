package ru.argustelecom.box.env.document.type;

import ru.argustelecom.box.env.billing.bill.model.SummaryBillAnalyticType;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class SummaryBillAnalyticTypeDtoTranslator
		implements DefaultDtoTranslator<SummaryBillAnalyticTypeDto, SummaryBillAnalyticType> {
	@Override
	public SummaryBillAnalyticTypeDto translate(SummaryBillAnalyticType summaryBillAnalyticType) {
		return new SummaryBillAnalyticTypeDto(summaryBillAnalyticType.getId(), summaryBillAnalyticType.getName(),
				summaryBillAnalyticType.getDescription(), summaryBillAnalyticType.getAvailableForCustomPeriod());
	}
}
