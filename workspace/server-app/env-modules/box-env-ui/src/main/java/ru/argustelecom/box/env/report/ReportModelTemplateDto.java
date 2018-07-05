package ru.argustelecom.box.env.report;

import java.util.Collection;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class ReportModelTemplateDto extends ConvertibleDto {
	private Long id;
	private String name;
	private String mimeType;
	private Collection<ReportOutputFormat> availableOutputFormats;

	@Builder
	public ReportModelTemplateDto(Long id, String name, String mimeType,
			Collection<ReportOutputFormat> availableOutputFormats) {
		this.id = id;
		this.name = name;
		this.mimeType = mimeType;
		this.availableOutputFormats = availableOutputFormats;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return ReportModelTemplateDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return ReportModelTemplate.class;
	}
}
