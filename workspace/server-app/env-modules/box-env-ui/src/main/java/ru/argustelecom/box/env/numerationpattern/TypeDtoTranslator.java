package ru.argustelecom.box.env.numerationpattern;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class TypeDtoTranslator implements DefaultDtoTranslator<TypeDto, Type> {

	@Override
	public TypeDto translate(Type type) {
		return new TypeDto(type.getId(), type.getObjectName(), type.getClass());
	}

}