package ru.argustelecom.box.env.report;

import java.util.stream.Collectors;

import javax.inject.Inject;

import ru.argustelecom.box.env.document.type.ReportModelTemplateDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ReportTypeDtoTranslator implements DefaultDtoTranslator<ReportTypeDto, ReportType> {

	@Inject
	private ReportModelTemplateDtoTranslator reportModelTemplateDtoTr;

	@Inject
	private ReportTypeGroupDtoTranslator reportTypeGroupDtoTr;

	@Override
	public ReportTypeDto translate(ReportType reportType) {
		// @formatter:off
		return new ReportTypeDto(
				reportType.getId(),
				reportType.getObjectName(),
				reportType.getDescription(),
				reportType.getGroup() != null ? reportTypeGroupDtoTr.translate(reportType.getGroup()) : null, 
				reportType.getTemplates().stream().map(t -> reportModelTemplateDtoTr.translate(t)).collect(Collectors.toList()),
				reportType.getRootBand().getId(),
				reportType.getState());
		// @formatter:on
	}

}
