package ru.argustelecom.box.env.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.report.model.ReportTypeGroup;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class ReportTypeGroupDto extends ConvertibleDto {

	private Long id;
	private String keyword;
	private ReportTypeGroupDto parent;
	private String objectName;

	@Override
	public Class<ReportTypeGroupDtoTranslator> getTranslatorClass() {
		return ReportTypeGroupDtoTranslator.class;
	}

	@Override
	public Class<ReportTypeGroup> getEntityClass() {
		return ReportTypeGroup.class;
	}
}
