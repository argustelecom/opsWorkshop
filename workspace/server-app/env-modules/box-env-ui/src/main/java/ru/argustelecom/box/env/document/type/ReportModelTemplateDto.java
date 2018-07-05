package ru.argustelecom.box.env.document.type;

import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "id", "fileName" }, callSuper = false)
public class ReportModelTemplateDto extends ConvertibleDto {
	private Long id;
	private String fileName;
	private String mimeType;
	private String description;
	private Date creationDate;

	@Builder
	public ReportModelTemplateDto(Long id, String fileName, String mimeType, String description, Date creationDate) {
		this.id = id;
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.description = description;
		this.creationDate = creationDate;
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
