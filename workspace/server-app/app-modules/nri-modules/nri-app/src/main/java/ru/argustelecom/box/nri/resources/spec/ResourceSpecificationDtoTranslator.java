package ru.argustelecom.box.nri.resources.spec;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Iterables.isEmpty;
import static java.util.stream.Collectors.toList;

/**
 * Транслятор спецификации ресурса
 * Created by s.kolyada on 19.09.2017.
 */
@DtoTranslator
public class ResourceSpecificationDtoTranslator
		implements DefaultDtoTranslator<ResourceSpecificationDto, ResourceSpecification> {

	/**
	 * Транслятор параметров
	 */
	@Inject
	private ParameterSpecificationDtoTranslator parameterSpecificationDtoTranslator;

	@Override
	public ResourceSpecificationDto translate(ResourceSpecification businessObject) {
		if (businessObject == null)
			return null;
		return ResourceSpecificationDto.builder()
				.id(businessObject.getId())
				.name(businessObject.getName())
				.image(businessObject.getImage())
				.isIndependent(businessObject.getIsIndependent())
				.childSpecifications(translateSpecList(businessObject.getChildSpecifications()))
				.parameters(translateParamSpecList(businessObject.getParameters()))
				.supportedLogicalResources(Optional.ofNullable(businessObject.getSupportedLogicalResources())
						.orElse(new HashSet<>()))
				.supportedPortTypes(businessObject.getSupportedPortTypes())
				.build();
	}

	/**
	 * Трансляция списка
	 * @param specifications список спецификаций
	 * @return список ДТО спецификаций
	 */
	public List<ResourceSpecificationDto> translateSpecList(List<ResourceSpecification> specifications) {
		return isEmpty(specifications) ? new ArrayList<>() :
				specifications.stream().map(this::translate).collect(toList());
	}

	/**
	 * Трансляция списка параметров
	 * @param specifications список спецификаций
	 * @return список ДТО спецификаций
	 */
	public List<ParameterSpecificationDto> translateParamSpecList(List<ParameterSpecification> specifications) {
		return isEmpty(specifications) ? new ArrayList<>() :
				specifications.stream().map(parameterSpecificationDtoTranslator::translate).collect(toList());
	}
}
