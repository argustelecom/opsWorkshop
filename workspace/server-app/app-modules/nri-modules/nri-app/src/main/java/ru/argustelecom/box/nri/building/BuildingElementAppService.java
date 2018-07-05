package ru.argustelecom.box.nri.building;

import org.apache.commons.lang3.Validate;
import ru.argustelecom.box.env.address.LocationAppService;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.building.model.BuildingElementType;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDtoTranslator;
import ru.argustelecom.box.nri.coverage.ResourceInstallationRepository;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Сервис для работы с элементами зданий
 * Created by s.kolyada on 23.08.2017.
 */
@ApplicationService
public class BuildingElementAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Транслятор из сущности в дто
	 */
	@Inject
	private BuildingElementDtoTranslator translator;

	/**
	 * Репозиторий типов элементов строений
	 */
	@Inject
	private BuildingElementTypeRepository typeRepository;

	/**
	 * Репозиторий доступа
	 */
	@Inject
	private BuildingElementRepository repository;

	/**
	 * репозиторий точки монтирования
	 */
	@Inject
	private ResourceInstallationRepository resInstallationRepository;

	/**
	 * транслятор точки монтирования
	 */
	@Inject
	private ResourceInstallationDtoTranslator resInstallationTranslator;

	/**
	 * Сервис для доступа к адресам
	 */
	@Inject
	private LocationAppService locationAppService;

	/**
	 * Получить строение расположенное по адресу
	 *
	 * @param location расположение
	 * @return элемент строения
	 */
	public BuildingElementDto findElementByLocation(Location location) {
		return translator.translate(repository.findElementByLocation(location));
	}

	/**
	 * Получить элемент по айди
	 *
	 * @param id айди элемента
	 * @return найденный элемент
	 */
	public BuildingElement findElementById(@Nonnull Long id) {
		return repository.findElementById(id);
	}

	/**
	 * Создать новый элемент
	 *
	 * @param newElement новый элемента
	 * @param parentDto  родительский элемент
	 * @return новый элемент
	 */
	public BuildingElementDto createNewElement(@Nonnull BuildingElementDto newElement, BuildingElementDto parentDto) {
		BuildingElement parent = parentDto == null ? null : findElementById(parentDto.getId());
		BuildingElement buildingElement = repository.create(newElement.getName(),
				typeRepository.findOne(newElement.getType().getId()), newElement.getLocation(), parent);
		return translator.translate(buildingElement);
	}

	/**
	 * Создать новый элемент с его дочерними
	 *
	 * @param newElement новый элемента
	 * @param parentDto  родительский элемент
	 * @return новый элемент
	 */
	public BuildingElementDto createNewElementWithChildren(@Nonnull BuildingElementDto newElement, BuildingElementDto parentDto) {
		BuildingElement parent = parentDto == null ? null : findElementById(parentDto.getId());

		BuildingElement buildingElement = repository.create(newElement.getName(),
				typeRepository.findOne(newElement.getType().getId()), newElement.getLocation(), parent);

		for (BuildingElementDto element : newElement.getChildElements()) {
			repository.create(element.getName(), typeRepository.findOne(element.getType().getId()),
					element.getLocation(), buildingElement);
		}

		return translator.translate(buildingElement);
	}


	/**
	 * Удалить элемент
	 *
	 * @param id идентификатор элемента
	 */
	public void delete(@Nonnull Long id) {
		repository.delete(id);
	}

	/**
	 * Изменить родителя у элемента
	 *
	 * @param changedElement изменяемый элемент
	 * @param parentElement  новый предок
	 */
	public void changeElementParent(@Nonnull BuildingElementDto changedElement, BuildingElementDto parentElement) {
		repository.changeParent(changedElement.getId(), parentElement.getId());
	}

	/**
	 * Изменить имя и тип элемента
	 *
	 * @param id            айди элемента
	 * @param name          новое имя
	 * @param elementTypeId айди типа
	 * @return новый элемент
	 */
	public BuildingElementDto changeNameAndType(@Nonnull Long id, @Nonnull String name, @Nonnull Long elementTypeId) {
		return translator.translate(repository.updateNameAndType(id, name, typeRepository.findOne(elementTypeId)));
	}

	/**
	 * Обновить расположение
	 *
	 * @param id       айди элемента
	 * @param location новое расположение
	 * @return обновленный элемент
	 */
	public BuildingElementDto changeLocation(@Nonnull Long id, Location location) {
		return translator.translate(repository.updateLocation(id, location));
	}

	/**
	 * Найти лемент строения точки монтирования по установке ресурса
	 *
	 * @param resInstallation установка ресурса
	 * @return элемент строения
	 */
	public BuildingElementDto findBuildingByResInstallation(ResourceInstallationDto resInstallation) {
		BuildingElement installedAt = repository.findElementById(resInstallation.getInstalledAt().getId());
		BuildingElement parent = installedAt;
		while (parent.getParent() != null)
			parent = parent.getParent();
		return translator.translate(parent);
	}

	/**
	 * находит все свободные адреса
	 *
	 * @param possibleLodgings все адреса строения
	 * @param root             строение
	 * @return все свободные адреса
	 */
	public List<Lodging> getFreeLodgings(List<Lodging> possibleLodgings, BuildingElementDto root) {
		Map<Long, Lodging> occupiedLodgings = flatten(root)
				.map(BuildingElementDto::getLocation)
				.filter(location -> location instanceof Lodging)
				.map(lodging -> (Lodging) lodging)
				.distinct()
				.collect(toMap(BusinessObject::getId, lodging -> lodging, (k1, k2) -> k1));
		return possibleLodgings.stream()
				.filter(lodging -> !occupiedLodgings.containsKey(lodging.getId()))
				.collect(toList());
	}

	/**
	 * создает стрим элементов дерева
	 *
	 * @param element рут
	 * @return стрим элементов дерева
	 */
	private Stream<BuildingElementDto> flatten(@Nonnull BuildingElementDto element) {
		return Stream.concat(Stream.of(element), element.getChildElements().stream().flatMap(this::flatten));
	}

	/**
	 * получает все инсталляции, располагающиеся в елементе, либо в его детях
	 *
	 * @param element элемент, в котором будем искать
	 * @return список точек монтирования
	 */
	public List<ResourceInstallationDto> getResourceInstallations(BuildingElementDto element) {
		List<Long> ids = flatten(element).map(BuildingElementDto::getId).collect(toList());
		return resInstallationRepository.findByInstalledAtIdIn(ids).stream()
				.map(resInstallationTranslator::translate)
				.collect(toList());
	}

	/**
	 * Получает список элементов строений по части адреса
	 *
	 * @param location часть адреса
	 * @return список строений
	 */
	public List<BuildingElementDto> findAllByLocationName(String location) {
		List<Location> locationsLike = locationAppService.getLocationsLike(location, 20);
		return repository.findAllByLocation(locationsLike)
				.stream()
				.map(translator::translate)
				.collect(toList());
	}

	/**
	 * Получить адрес элемента строения
	 *
	 * @param buildingElement элемент строения
	 * @return адрес
	 */
	public Location findBuildingElementLocation(BuildingElementDto buildingElement) {
		Validate.isTrue(buildingElement != null);
		BuildingElement element = findElementById(buildingElement.getId());
		if (element == null) {
			throw new IllegalStateException("No building element found with id = " + buildingElement.getId());
		}
		Location res = element.getLocation();
		while (res == null && element.getParent() != null) {
			element = element.getParent();
			res = element.getLocation();
		}
		return res;
	}

	/**
	 * Изменить тип элемента строения
	 *
	 * @param moveFromId ID заменяемого типа
	 * @param moveToId   ID нового типа
	 */
	public void changeType(Long moveFromId, Long moveToId) {
		BuildingElementType from = typeRepository.findOne(moveFromId);
		BuildingElementType to = typeRepository.findOne(moveToId);
		if (from != null && to != null) {
			List<BuildingElement> elementsToChange = repository.findAllByElementType(from);
			elementsToChange.forEach(el -> repository.changeType(el, to));
		}
	}
}
