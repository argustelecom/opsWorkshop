package ru.argustelecom.box.nri.resources.requirements;

import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Транслятор требования к ресурсам в ДТО
 * Created by s.kolyada on 19.09.2017.
 */
@DtoTranslator
public class ResourceSchemaDtoTranslator
		implements DefaultDtoTranslator<ResourceSchemaDto, ResourceSchema> {


	@Inject
	private RequiredItemDtoTranslator translator;

	@Override
	public ResourceSchemaDto translate(ResourceSchema businessObject) {
		if (businessObject == null) {
			return null;
		}
		return ResourceSchemaDto.builder()
				.id(businessObject.getId())
				.requirements(translateItems(businessObject.getRequirements()))
				.name(businessObject.getName())
				.build();
	}

	/**
	 * Транслировать элементы требований
	 * @param items элементы
	 * @return ДТО элементов требований
	 */
	public List<RequiredItemDto> translateItems(List<RequiredItem> items) {
		List<RequiredItemDto> res = new ArrayList<>();

		if (CollectionUtils.isEmpty(items)) {
			return res;
		}

		for (RequiredItem item : items) {
			res.add(translator.translate(item));
		}
		return res;
	}
}
