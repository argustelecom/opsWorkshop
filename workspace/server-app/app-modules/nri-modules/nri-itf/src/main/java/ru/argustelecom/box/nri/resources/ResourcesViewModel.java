package ru.argustelecom.box.nri.resources;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.argustelecom.box.env.address.LocationAppService;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceAppService;
import ru.argustelecom.box.nri.resources.model.ResourceStatus;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationAppService;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Контроллер страницы ресурсов
 * @author a.wisniewski
 * @since 20.09.2017
 */
@Named(value = "resourcesVM")
@PresentationModel
public class ResourcesViewModel extends ViewModel {

	private static final long serialVersionUID = 2388116638878841L;

	/**
	 * Состояние страницы
	 */
	@Inject
	private ResourcesViewState viewState;

	/**
	 * Сервис спецификаций ресурсов
	 */
	@Inject
	private ResourceSpecificationAppService resSpecService;

	/**
	 * Сервис адресов
	 */
	@Inject
	private LocationAppService locationService;

	/**
	 * Сервис ресурсов
	 */
	@Inject
	private ResourceInstanceAppService resService;

	/**
	 * Ресурсы, полученных в результате поиска
	 */
	@Inject
	@Getter
	private ResourceInstanceList lazyResources;

	/**
	 * Спецификации ресурсов, доступные для выбора в SelectUneMenu при поиске по параметрам
	 */
	@Getter
	private List<ResourceSpecificationDto> availableSpecifications = new ArrayList<>();

	/**
	 * Рутовые спецификации (способные самостоятельно существовать)
	 */
	@Getter
	private List<ResourceSpecificationDto> rootSpecifications = new ArrayList<>();

	/**
	 * Доступные для поиска параметры ресурсов
	 */
	@Getter
	@Setter
	private List<ParameterSpecificationDto> availableParams = new ArrayList<>();

	/**
	 * используется ли поиск по дополнительным фильтрам (поиск по парамиетрам)
	 */
	@Getter
	@Setter
	private boolean useAdditionalFilters = false;

	/**
	 * Идентифтикатор спецификации, по которой создаём вложенный ресурс
	 */
	@Getter
	@Setter
	private Long newElemSpecificationId;

	/**
	 * выбранный ресурс
	 */
	@Getter
	@Setter
	private ResourceInstanceListDto selectedResource;


	@Override
	@PostConstruct
	public void postConstruct() {
		availableSpecifications = resSpecService.findAllSpecifications();
		rootSpecifications = availableSpecifications.stream()
				.filter(ResourceSpecificationDto::getIsIndependent)
				.collect(toList());
		availableParams = rootSpecifications.stream()
				.flatMap(resourceSpec -> resourceSpec.getParameters().stream())
				.distinct()
				.collect(toList());
		unitOfWork.makePermaLong();
	}

	/**
	 * автокомплит адреса
	 * @param location адрес в виде строки с элементами через запятую
	 * @return список полных адресов в виде String
	 */
	public List<String> completeLocation(String location) {
		if (StringUtils.isEmpty(location) || location.length() < 3)
			return emptyList();
		List<Location> locations = locationService.getLocationsLike(location, 5);
		return locations.stream()
				.flatMap(loc -> Stream.concat(
						Stream.of(loc),
						loc.getChildren().stream().sorted(comparing(Location::getName)).limit(10)))
				.map(loc -> getName(loc) + ", ")
				.sorted()
				.collect(toList());
	}

	/**
	 * получает имя location'а
	 * @param location location
	 * @return имя location'a
	 */
	private String getName(Location location) {
		return location.getParent() == null ? location.getName() :
				getName(location.getParent()) + ", " + location.getName();
	}

	/**
	 * добавляет к уже введенному адресу свежезагруженный элемент (автокомплит)
	 * @param s событие - изменение значения в input'e адреса
	 */
	public void updateLocator(ValueChangeEvent s) {
		viewState.setLocationString(viewState.getLocationString() + s.getNewValue());
	}

	/**
	 * Очистить параметры создания
	 */
	public void cleanCreationParams() {
		newElemSpecificationId = null;
	}

	/**
	 * скрыть/раскрыть дполнительные фильтры (фильтры по параметрам)
	 */
	public void toggleAdditionalFilters() {
		if (useAdditionalFilters) {
			viewState.getParamDescriptors().clear();
		} else {
			viewState.getParamDescriptors().add(new ParamDescriptorDto());
		}
		useAdditionalFilters = !useAdditionalFilters;
	}

	/**
	 * удалить выбранный ресурс
	 */
	public void deleteSelectedResource() {
		resService.removeResource(selectedResource.getId());
		selectedResource = null;
		lazyResources.reloadData();
	}

	/**
	 * добавить еще один фильтр по параметру
	 */
	public void addParamFilter() {
		viewState.getParamDescriptors().add(new ParamDescriptorDto());
	}

	/**
	 * получает возможные статусы ресурса
	 * @return возможные статусы ресурса
	 */
	public List<ResourceStatus> getStatuses() {
		return asList(ResourceStatus.values());
	}

	/**
	 * удалить параметр по индексу
	 * @param index индекс
	 */
	public void deleteParam(int index) {
		if (index < viewState.getParamDescriptors().size())
			viewState.getParamDescriptors().remove(index);
	}
}
