package ru.argustelecom.box.env.billing.bill.dto;

import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ReportModelTemplateDtoTranslator implements DefaultDtoTranslator<ReportModelTemplateDto, ReportModelTemplate> {
	@Override
	public ReportModelTemplateDto translate(ReportModelTemplate reportModelTemplate) {
		//@formatter:off
		return ReportModelTemplateDto.builder()
				.id(reportModelTemplate.getId())
				.fileName(reportModelTemplate.getFileName())
				.mimeType(reportModelTemplate.getMimeType())
				.description(reportModelTemplate.getDescription())
				.creationDate(reportModelTemplate.getCreationDate())
				.build();
		//@formatter:on
	}
}
