package ru.argustelecom.box.nri.resources;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;

/**
 * Транслятор в дто для списка ресурсов
 * @author a.wisniewski
 * @since 11.10.2017
 */
@DtoTranslator
public class ResourceInstanceListDtoTranslator
		implements DefaultDtoTranslator<ResourceInstanceListDto, ResourceInstance> {

	@Override
	public ResourceInstanceListDto translate(ResourceInstance businessObject) {
		return ResourceInstanceListDto.builder()
				.id(businessObject.getId())
				.name(businessObject.getName())
				.specification(businessObject.getSpecification().getName())
				.status(businessObject.getStatus()).build();
	}
}
