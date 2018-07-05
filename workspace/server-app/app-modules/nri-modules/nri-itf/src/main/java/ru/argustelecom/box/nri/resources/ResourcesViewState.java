package ru.argustelecom.box.nri.resources;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.nri.resources.model.ResourceStatus;
import ru.argustelecom.system.inf.page.PresentationState;

import java.util.ArrayList;
import java.util.List;

import static ru.argustelecom.box.nri.resources.ResourcesViewState.ResourcesFilter.ADDRESS;
import static ru.argustelecom.box.nri.resources.ResourcesViewState.ResourcesFilter.NAME;
import static ru.argustelecom.box.nri.resources.ResourcesViewState.ResourcesFilter.PARAMS;
import static ru.argustelecom.box.nri.resources.ResourcesViewState.ResourcesFilter.SPECIFICATION;
import static ru.argustelecom.box.nri.resources.ResourcesViewState.ResourcesFilter.STATUS;

/**
 * Состояние страницы ресурсов
 * @author a.wisniewski
 * @since 20.09.2017
 */
@PresentationState
@Getter
@Setter
public class ResourcesViewState extends FilterViewState {

	private static final long serialVersionUID = 1L;

	/**
	 * Имя
	 */
	@FilterMapEntry(NAME)
	private String name;

	/**
	 * Адрес
	 */
	@FilterMapEntry(ADDRESS)
	private String locationString;

	/**
	 * Статус ресурса
	 */
	@FilterMapEntry(STATUS)
	private ResourceStatus resourceStatus;

	/**
	 * Имя спецификации ресурса
	 */
	@FilterMapEntry(SPECIFICATION)
	private String specificationName;

	/**
	 * Список описателей параметров ресурса, по которым будем искать ресурсы
	 */
	@FilterMapEntry(PARAMS)
	private List<ParamDescriptorDto> paramDescriptors = new ArrayList<>();

	/**
	 * enum для фильтрации
	 */
	class ResourcesFilter {
		static final String NAME = "NAME";
		static final String ADDRESS = "ADDRESS";
		static final String STATUS = "STATUS";
		static final String SPECIFICATION = "SPECIFICATION";
		static final String PARAMS = "PARAMS";

		/**
		 * Приватный конструктор
		 */
		private ResourcesFilter() {
		}
	}

}
