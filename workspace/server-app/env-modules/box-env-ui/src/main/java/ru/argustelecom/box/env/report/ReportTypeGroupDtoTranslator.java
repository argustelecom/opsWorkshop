package ru.argustelecom.box.env.report;

import javax.inject.Inject;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.report.model.ReportTypeGroup;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ReportTypeGroupDtoTranslator implements DefaultDtoTranslator<ReportTypeGroupDto, ReportTypeGroup> {

	@Inject
	private ReportTypeGroupDtoTranslator reportTypeGroupDtoTr;

	@Override
	public ReportTypeGroupDto translate(ReportTypeGroup reportTypeGroup) {
		// @formatter:off
		return new ReportTypeGroupDto(
				reportTypeGroup.getId(),
				reportTypeGroup.getKeyword(),
				reportTypeGroup.getParent() != null ? reportTypeGroupDtoTr.translate(reportTypeGroup.getParent()) : null,
				reportTypeGroup.getObjectName()
		);
		// @formatter:on
	}

}
