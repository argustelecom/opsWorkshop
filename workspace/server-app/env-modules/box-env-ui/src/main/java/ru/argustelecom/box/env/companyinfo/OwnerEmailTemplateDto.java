package ru.argustelecom.box.env.companyinfo;

import java.sql.Blob;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "id", "templateName" }, callSuper = false)
public class OwnerEmailTemplateDto extends ConvertibleDto {

	private Long id;
	private String templateName;
	private Blob template;

	@Builder
	public OwnerEmailTemplateDto(Long id, String templateName, Blob template) {
		this.id = id;
		this.templateName = templateName;
		this.template = template;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return OwnerEmailTemplateDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Owner.class;
	}
}