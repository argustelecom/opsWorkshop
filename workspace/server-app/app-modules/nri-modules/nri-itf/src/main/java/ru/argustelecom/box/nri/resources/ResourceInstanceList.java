package ru.argustelecom.box.nri.resources;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.model.ResourceInstance_;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification_;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static ru.argustelecom.box.nri.resources.ResourceInstanceList.ResourceInstanceSort;
import static ru.argustelecom.box.nri.resources.model.ResourceInstance.ResourceInstanceQuery;

/**
 * Ленивый список ресурсов
 * @author a.wisniewski
 * @since 11.10.2017
 */
public class ResourceInstanceList extends EQConvertibleDtoLazyDataModel<ResourceInstance, ResourceInstanceListDto,
		ResourceInstanceQuery, ResourceInstanceSort> {

	private static final long serialVersionUID = 6197587362636132921L;

	/**
	 * транслятор
	 */
	@Inject
	private ResourceInstanceListDtoTranslator resourceListDtoTranslator;

	/**
	 * фильтры
	 */
	@Inject
	private ResourceInstanceListFilterModel resourceListFilterModel;

	@PostConstruct
	private void postConstruct() {
		// связываем сортировочный enum с полями ресурса
		addPath(ResourceInstanceSort.NAME, query -> query.root().get(ResourceInstance_.name));
		addPath(ResourceInstanceSort.SPECIFICATION, query -> query.root()
				.get(ResourceInstance_.specification)
				.get(ResourceSpecification_.name));
		addPath(ResourceInstanceSort.STATUS, query -> query.root().get(ResourceInstance_.status));
	}

	@Override
	protected Class<ResourceInstanceSort> getSortableEnum() {
		return ResourceInstanceSort.class;
	}

	@Override
	protected DefaultDtoTranslator<ResourceInstanceListDto, ResourceInstance> getDtoTranslator() {
		return resourceListDtoTranslator;
	}

	@Override
	protected EQConvertibleDtoFilterModel<ResourceInstance.ResourceInstanceQuery> getFilterModel() {
		return resourceListFilterModel;
	}

	/**
	 * Enum для сортировок по колонкам
	 */
	public enum ResourceInstanceSort {
		NAME, SPECIFICATION, STATUS
	}
}

