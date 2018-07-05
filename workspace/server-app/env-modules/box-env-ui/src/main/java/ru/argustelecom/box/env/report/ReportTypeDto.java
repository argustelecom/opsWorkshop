package ru.argustelecom.box.env.report;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.document.type.ReportModelTemplateDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.report.model.ReportTypeState;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class ReportTypeDto extends ConvertibleDto {

	private Long id;
	private String objectName;
	private String description;
	private ReportTypeGroupDto reportTypeGroup;
	private List<ReportModelTemplateDto> templates;
	private Long reportBandModelId;
	private ReportTypeState state;

	@Override
	public Class<ReportTypeDtoTranslator> getTranslatorClass() {
		return ReportTypeDtoTranslator.class;
	}

	@Override
	public Class<ReportType> getEntityClass() {
		return ReportType.class;
	}
}
