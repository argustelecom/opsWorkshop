package ru.argustelecom.box.nri.resources.spec;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;

/**
 * Транслятор спецификации параметра
 * Created by s.kolyada on 19.09.2017.
 */
@DtoTranslator
public class ParameterSpecificationDtoTranslator
		implements DefaultDtoTranslator<ParameterSpecificationDto, ParameterSpecification> {

	@Override
	public ParameterSpecificationDto translate(ParameterSpecification businessObject) {
		if (businessObject == null)
			return null;
		return ParameterSpecificationDto.builder()
				.id(businessObject.getId())
				.name(businessObject.getName())
				.dataType(businessObject.getDataType())
				.defaultValue(businessObject.getDefaultValue())
				.regex(businessObject.getRegex())
				.required(businessObject.getRequired())
				.build();
	}
}
