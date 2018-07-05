package ru.argustelecom.box.env.billing.bill.dto;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.dto2.DefaultDtoConverterUtils;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class BillTypeDtoTranslator implements DefaultDtoTranslator<BillTypeDto, BillType> {

	@Inject
	private ReportModelTemplateDtoTranslator reportModelTemplateDtoTranslator;

	@Override
	public BillTypeDto translate(BillType billType) {
		//@formatter:off
		return BillTypeDto.builder()
				.id(billType.getId())
				.name(billType.getObjectName())
				.reportTemplates(DefaultDtoConverterUtils.translate(reportModelTemplateDtoTranslator,
						billType.getTemplates()))
				.build();
		//@formatter:on
	}

}