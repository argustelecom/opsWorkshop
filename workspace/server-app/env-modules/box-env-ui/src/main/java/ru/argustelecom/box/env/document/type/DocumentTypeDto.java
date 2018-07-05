package ru.argustelecom.box.env.document.type;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "id", "name" }, callSuper = false)
public abstract class DocumentTypeDto extends ConvertibleDto {
	private Long id;
	private String name;
	private List<ReportModelTemplateDto> reportModelTemplates;

	public DocumentTypeDto(Long id, String name, List<ReportModelTemplateDto> reportModelTemplates) {
		this.id = id;
		this.name = name;
		this.reportModelTemplates = reportModelTemplates;
	}
}
