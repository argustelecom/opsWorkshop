package ru.argustelecom.box.env.companyinfo;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.OwnerParameter;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class OwnerAdditionalParameterDtoTranslator
		implements DefaultDtoTranslator<OwnerAdditionalParameterDto, OwnerParameter> {
	@Override
	public OwnerAdditionalParameterDto translate(OwnerParameter param) {
		//@formatter:off
		return OwnerAdditionalParameterDto.builder()
					.id(param.getId())
					.name(param.getObjectName())
					.keyword(param.getKeyword())
					.value(param.getValue())
				.build();
		//@formatter:on
	}
}
