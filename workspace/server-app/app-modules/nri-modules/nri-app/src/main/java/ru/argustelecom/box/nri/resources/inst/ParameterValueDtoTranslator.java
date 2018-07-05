package ru.argustelecom.box.nri.resources.inst;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.nri.resources.model.ParameterValue;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDtoTranslator;

import javax.inject.Inject;

/**
 * ДТО транслятор для значений параметров
 *
 * @author a.wisniewski
 * @since 19.09.2017
 */
public class ParameterValueDtoTranslator implements DefaultDtoTranslator<ParameterValueDto, ParameterValue> {

	/**
	 * Транслятор спецификаций параметров
	 */
	@Inject
	private ParameterSpecificationDtoTranslator specTranslator;

	@Override
	public ParameterValueDto translate(ParameterValue businessObject) {
		if (businessObject == null)
			return null;
		return ParameterValueDto.builder()
				.id(businessObject.getId())
				.specification(specTranslator.translate(businessObject.getSpecification()))
				.value(businessObject.getValue())
				.build();
	}
}
