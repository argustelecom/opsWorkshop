package ru.argustelecom.box.nri.resources.requirements;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDtoTranslator;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Транслятор требования к наличию ресурса в ДТО
 * Created by s.kolyada on 19.09.2017.
 */
@DtoTranslator
public class RequiredItemDtoTranslator
		implements DefaultDtoTranslator<RequiredItemDto, RequiredItem> {

	/**
	 * Транслятор спец рес
	 */
	@Inject
	private ResourceSpecificationDtoTranslator specTranslator;

	/**
	 * Транслятор требований к знач пар
	 */
	@Inject
	private RequiredParameterValueDtoTranslator paramTranslator;

	@Override
	public RequiredItemDto translate(RequiredItem businessObject) {
		if (businessObject == null) {
			return null;
		}

		List<RequiredParameterValueDto> parameterRequirements = businessObject.getRequiredParameters()
				.stream()
				.map(paramTranslator::translate)
				.collect(Collectors.toList());

		return RequiredItemDto.builder()
				.id(businessObject.getId())
				.resourceSpecification(specTranslator.translate(businessObject.getResourceSpecification()))
				.requiredParameters(parameterRequirements)
				.children(businessObject.getChildren().stream().map(this::translate).collect(Collectors.toList()))
				.build();
	}
}
