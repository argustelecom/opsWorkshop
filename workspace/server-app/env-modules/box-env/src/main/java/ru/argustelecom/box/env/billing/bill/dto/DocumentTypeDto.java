package ru.argustelecom.box.env.billing.bill.dto;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;

@Getter
@Setter
@EqualsAndHashCode(of = { "id", "name" }, callSuper = false)
public abstract class DocumentTypeDto extends ConvertibleDto {
	private Long id;
	private String name;
	private List<ReportModelTemplateDto> reportTemplates;

	public DocumentTypeDto(Long id, String name, List<ReportModelTemplateDto> reportTemplates) {
		this.id = id;
		this.name = name;
		this.reportTemplates = reportTemplates;
	}
}
