package ru.argustelecom.box.env.billing.bill.dto;

import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
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
