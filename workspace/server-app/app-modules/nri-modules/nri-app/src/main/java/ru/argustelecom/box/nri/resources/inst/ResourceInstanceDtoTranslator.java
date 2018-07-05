package ru.argustelecom.box.nri.resources.inst;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDtoTranslator;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDtoTranslator;

import javax.inject.Inject;

import static java.util.stream.Collectors.toList;

/**
 * ДТО транслятор для ресурсов
 *
 * @author a.wisniewski
 * @since 19.09.2017
 */
@DtoTranslator
public class ResourceInstanceDtoTranslator implements
		DefaultDtoTranslator<ResourceInstanceDto, ResourceInstance> {

	/**
	 * Транслятор спецификаций
	 */
	@Inject
	private ResourceSpecificationDtoTranslator specTranslator;

	/**
	 * Транслятор параметров
	 */
	@Inject
	private ParameterValueDtoTranslator paramTranslator;

	/**
	 * Транслятор логических ресурсов
	 */
	@Inject
	private LogicalResourceDtoTranslator logicalResourceDtoTranslator;

	@Override
	public ResourceInstanceDto translate(ResourceInstance businessObject) {
		if (businessObject == null)
			return null;
		return ResourceInstanceDto.builder()
				.id(businessObject.getId())
				.status(businessObject.getStatus())
				.name(businessObject.getName())
				.specification(specTranslator.translate(businessObject.getSpecification()))
				.children(businessObject.getChildren().stream().map(this::translate).collect(toList()))
				.parameterValues(businessObject.getParameterValues().stream()
						.map(paramTranslator::translate).collect(toList()))
				.logicalResources(businessObject.getLogicalResources().stream()
						.map(logicalResourceDtoTranslator::translate).collect(toList()))
				.build();
	}

	/**
	 * Только имя и статус
	 *
	 * @param businessObject бизнес объект
	 * @return обрезанное дто
	 */
	public ResourceInstanceDto translateLazy(ResourceInstance businessObject) {
		if (businessObject == null)
			return null;
		return ResourceInstanceDto.builder()
				.id(businessObject.getId())
				.status(businessObject.getStatus())
				.name(businessObject.getName())
				.build();
	}
}
