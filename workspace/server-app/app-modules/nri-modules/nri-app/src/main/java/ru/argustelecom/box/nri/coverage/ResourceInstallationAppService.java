package ru.argustelecom.box.nri.coverage;

import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.building.BuildingElementDto;
import ru.argustelecom.box.nri.building.BuildingElementRepository;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.box.nri.resources.ResourceInstanceRepository;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Сервис для работы с точками монтирования ресурсов
 * Created by s.kolyada on 31.08.2017.
 */
@ApplicationService
public class ResourceInstallationAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Транслятор из Entity в DTO
	 */
	@Inject
	private ResourceInstallationDtoTranslator translator;

	/**
	 * Репозиторий доступа к хранилищу точек монтирования
	 */
	@Inject
	private ResourceInstallationRepository repository;

	/**
	 * Репозиторий доступа к хранилищу элементов строений
	 */
	@Inject
	private BuildingElementRepository buildingElementRepository;

	/**
	 * Репозиторий доступа к хранилищу ресурсов
	 */
	@Inject
	private ResourceInstanceRepository resourceInstanceRepository;

	/**
	 * Найти все точки монтирования, принадлежащие элементу строения
	 *
	 * @param buildingElementId айди элемента строения
	 * @return список точек монтирования
	 */
	public List<ResourceInstallationDto> findAllByBuildingElement(@Nonnull Long buildingElementId) {
		BuildingElement buildingElement = buildingElementRepository.findElementById(buildingElementId);
		return repository.findAllByBuildingElement(buildingElement).stream()
				.map(translator::translate)
				.sorted(comparing(ResourceInstallationDto::getId))
				.collect(toList());
	}

	/**
	 * Найти все точки монтирования, принадлежащие элементу строения и всем его дочерним элементам
	 *
	 * @param buildingElementId айди элемента строения
	 * @return список точек монтирования
	 */
	public List<ResourceInstallationDto> findAllByParentBuildingElement(@Nonnull Long buildingElementId) {
		BuildingElement buildingElement = buildingElementRepository.findElementById(buildingElementId);
		return repository.findAllInstallationsByBuilding(buildingElement).stream()
				.map(translator::translate)
				.sorted(comparing(ResourceInstallationDto::getId))
				.collect(toList());
	}

	/**
	 * Найти все точки монтирования, в зону охвата которых входит элемент строения
	 *
	 * @param buildingElementId айди элемента строения
	 * @return список точек монтирования
	 */
	public List<ResourceInstallationDto> findAllCoveringBuildingElement(@Nonnull Long buildingElementId) {
		BuildingElement buildingElement = buildingElementRepository.findElementById(buildingElementId);
		return findAllCoveringElement(buildingElement).stream()
				.map(translator::translate)
				.sorted(comparing(ResourceInstallationDto::getId))
				.collect(toList());
	}

	/**
	 * Рекурсивно проходит по элементу и его родителям для получения списка ресурсов, покрывающих элемент
	 *
	 * @param element элемент для поиска покрытия
	 * @return список ресурсов, покрывающих родителей элемента и сам элемент
	 */
	private List<ResourceInstallation> findAllCoveringElement(BuildingElement element) {
		List<ResourceInstallation> cover = repository.findAllCover(element);
		if (element.getParent() != null)
			cover.addAll(findAllCoveringElement(element.getParent()));
		return cover;
	}

	/**
	 * Обновить точку монтирование
	 *
	 * @param installation    инсталляция
	 * @param buildingElement элемент строения
	 * @return обновлённая инсталляция
	 */
	public ResourceInstallationDto updateInstallationPoint(ResourceInstallationDto installation, BuildingElementDto buildingElement) {
		BuildingElement element = buildingElementRepository.findElementById(buildingElement.getId());
		return translator.translate(repository.setInstalledAt(installation.getId(), element));
	}

	/**
	 * Обновить комментариц к установке
	 *
	 * @param installation установка
	 * @param comment      новый комментарий
	 * @return обновлённый ДТО установки
	 */
	public ResourceInstallationDto updateInstallationComment(ResourceInstallationDto installation, String comment) {
		return translator.translate(repository.updateComment(installation.getId(), comment));
	}

	/**
	 * Обновить список покрываемых инсталляцией элементов
	 *
	 * @param installation инсталляция
	 * @return дтошка сохраненной инсталляции
	 */
	public ResourceInstallationDto updateInstallationCoveredElements(ResourceInstallationDto installation) {
		return translator.translate(repository.updateCoveredElements(
				installation.getId(),
				installation.getCover().stream()
						.map(BuildingElementDto::getId)
						.collect(toList())));
	}

	/**
	 * Найти установку по ресурсу
	 *
	 * @param resource ресурс
	 * @return установка
	 */
	public ResourceInstallationDto findInstallationByResource(ResourceInstanceDto resource) {
		return translator.translate(repository.findByResource(resource.getId()));
	}

	/**
	 * Удалить установку
	 *
	 * @param installation установка
	 */
	public void deleteInstallation(ResourceInstallationDto installation) {
		repository.delete(installation.getId());
	}

	/**
	 * Создать новую установку
	 *
	 * @param resourceId           id ресурса
	 * @param buildingElementDtoId id элемента строения
	 * @return созданная установка
	 */
	public ResourceInstallationDto createInstallation(@Nonnull Long resourceId, @Nonnull Long buildingElementDtoId) {
		BuildingElement buildingElement = buildingElementRepository.findElementById(buildingElementDtoId);
		ResourceInstance resource = resourceInstanceRepository.findOne(resourceId);
		if (resource.getId() != null)
			return translator.translate(repository.createInstallation(resource, buildingElement));
		return null;
	}

	public List<ResourceInstallationDto> findAllByBuilding(Building building) {
		BuildingElement buildingElement = buildingElementRepository.findElementByLocation(building);
		if (buildingElement == null) {
			return Collections.emptyList();
		}
		return findAllByParentBuildingElement(buildingElement.getId());
	}
}
