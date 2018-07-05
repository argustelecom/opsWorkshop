package ru.argustelecom.box.env.report;

import static ru.argustelecom.box.env.report.impl.utils.ReportOutputFormatMappings.getReportTypesBy;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.impl.yarg.FormatterFactory;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.inf.service.DtoTranslator;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@DtoTranslator
public class ReportModelTemplateDtoTranslator
		implements DefaultDtoTranslator<ReportModelTemplateDto, ReportModelTemplate> {

	@Inject
	private FormatterFactory formatterFactory;

	@Override
	public ReportModelTemplateDto translate(ReportModelTemplate template) {
		List<ReportOutputFormat> reportOutputFormats = new ArrayList<>(getReportTypesBy(template.getMimeType()));
		if (!formatterFactory.isPdfConverterPresent()) {
			reportOutputFormats.remove(ReportOutputFormat.PDF);
		}

		//@formatter:off
		return ReportModelTemplateDto.builder()
					.id(template.getId())
					.name(template.getFileName())
					.mimeType(template.getMimeType())
					.availableOutputFormats(reportOutputFormats)
				.build();
		//@formatter:on
	}
}
