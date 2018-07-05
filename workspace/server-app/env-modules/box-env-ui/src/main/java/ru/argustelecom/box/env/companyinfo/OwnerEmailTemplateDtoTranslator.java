package ru.argustelecom.box.env.companyinfo;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class OwnerEmailTemplateDtoTranslator implements DefaultDtoTranslator<OwnerEmailTemplateDto, Owner> {

	public OwnerEmailTemplateDto translate(Owner owner) {
		//@formatter:off
		return OwnerEmailTemplateDto.builder()
					.id(owner.getId())
					.templateName(owner.getEmailTemplateName())
					.template(owner.getEmailTemplate())
				.build();
		//@formatter:on
	}
}