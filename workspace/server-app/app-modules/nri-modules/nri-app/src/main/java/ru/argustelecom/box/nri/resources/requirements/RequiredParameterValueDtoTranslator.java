package ru.argustelecom.box.nri.resources.requirements;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDtoTranslator;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredParameterValue;

import javax.inject.Inject;

/**
 * Транслятор требования к параметру ресурса в ДТО
 * Created by s.kolyada on 19.09.2017.
 */
@DtoTranslator
public class RequiredParameterValueDtoTranslator
		implements DefaultDtoTranslator<RequiredParameterValueDto, RequiredParameterValue> {

	@Inject
	private ParameterSpecificationDtoTranslator translator;

	@Override
	public RequiredParameterValueDto translate(RequiredParameterValue businessObject) {
		if (businessObject == null) {
			return null;
		}
		return RequiredParameterValueDto.builder()
				.id(businessObject.getId())
				.compareAction(businessObject.getCompareAction())
				.value(businessObject.getRequiredValue())
				.parameterSpecification(translator.translate(businessObject.getParameterSpecification()))
				.build();
	}
}
