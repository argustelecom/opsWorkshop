package ru.argustelecom.box.nri.resources;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import ru.argustelecom.box.env.address.LocationAppService;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.nri.building.BuildingElementAppService;
import ru.argustelecom.box.nri.building.BuildingElementDto;
import ru.argustelecom.box.nri.coverage.ResourceInstallationAppService;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Фрейм с информацией об установке ресурса
 *
 * @author s.kolyada
 * @since 29.09.2017
 */
@Named(value = "resourceInstallationInfoFM")
@PresentationModel
public class ResourceInstallationInfoFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Создаваемый/редактируемый ресурс
	 */
	@Getter
	private ResourceInstanceDto resource;

	/**
	 * Установка
	 */
	@Getter
	private ResourceInstallationDto installation;

	/**
	 * Выбранный элемент строения
	 */
	@Getter
	@Setter
	private BuildingElementDto selectedBuildingElementDto;

	/**
	 * Строение
	 */
	@Getter
	private Location location;

	/**
	 * Сервис элементов строений
	 */
	@Inject
	private BuildingElementAppService buildingService;

	/**
	 * Сервис для работы с установками ресурсов
	 */
	@Inject
	private ResourceInstallationAppService installationAppService;

	/**
	 * Сервис адресов
	 */
	@Inject
	private LocationAppService locationService;

	/**
	 * Инициализирует фрейм
	 *
	 * @param resource русрс
	 */
	public void preRender(ResourceInstanceDto resource) {
		this.resource = resource;

		// ищем установку
		installation = installationAppService.findInstallationByResource(resource);

		// если установка есть, то догружаем информацию о строении
		location = findInstallationLocation(installation);
	}

	/**
	 * Создать инсталляцию
	 */
	public void createNewInstallation() {
		if (selectedBuildingElementDto != null) {
			installation = installationAppService.createInstallation(resource.getId(), selectedBuildingElementDto.getId());
			location = findInstallationLocation(installation);
			cleanCreationParams();
		}
	}

	/**
	 * Найти адрес установки ресурса
	 * @param installation утсановка ресурса
	 * @return адрес установки
	 */
	private Location findInstallationLocation(ResourceInstallationDto installation) {
		if (installation == null) {
			return null;
		}
		BuildingElementDto buildingElementDto = installation.getInstalledAt();
		Location result;
		if (buildingElementDto.getLocation() == null) {
			result = buildingService.findBuildingElementLocation(installation.getInstalledAt());
		} else {
			result = buildingElementDto.getLocation();
		}
		return result;
	}

	/**
	 * Автокомплит адреса
	 *
	 * @param location адрес в виде строки с элементами через запятую
	 * @return список полных адресов в виде String
	 */
	public List<BuildingElementDto> completeLocation(String location) {
		return buildingService.findAllByLocationName(location);
	}

	/**
	 * Получает имя location'а
	 *
	 * @param location location
	 * @return имя location'a
	 */
	public String getName(Location location) {
		if (location != null) {
			return location.getParent() == null ? location.getName() :
					getName(location.getParent()) + ", " + location.getName();
		} else {
			return null;
		}
	}

	/**
	 * listener изменения значения переменной в автодополнении
	 *
	 * @param event событие - выбор значения из списка автокомплита
	 */
	public void selectLocation(ValueChangeEvent event) {
		selectedBuildingElementDto = (BuildingElementDto) event.getNewValue();
	}

	/**
	 * listener выбора из списка адресов
	 *
	 * @param event событие - выбор значения из списка автокомплита
	 */
	public void selectLocation(SelectEvent event) {
		selectedBuildingElementDto = (BuildingElementDto) event.getObject();
	}

	/**
	 * Удаляем текущую исталляцию
	 */
	public void removeInstallation() {
		if (installation != null) {
			installationAppService.deleteInstallation(installation);
		}
	}

	/**
	 * Очистить параметры для создания
	 */
	public void cleanCreationParams() {
		selectedBuildingElementDto = null;
	}
}
