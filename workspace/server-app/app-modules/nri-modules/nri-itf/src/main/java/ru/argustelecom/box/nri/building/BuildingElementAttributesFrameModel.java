package ru.argustelecom.box.nri.building;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.LocationRepository;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Фрейм информации об элементе строения
 * Created by s.kolyada on 28.08.2017.
 */
@Named(value = "buildingElementAttributesFM")
@PresentationModel
public class BuildingElementAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис работы с элементами строений
	 */
	@Inject
	private BuildingElementAppService service;

	/**
	 * Сервис работы с типами элементов строений
	 */
	@Inject
	private BuildingElementTypeAppService typeService;

	/**
	 * Репозиторий доступа к адресным данным
	 */
	@Inject
	private LocationRepository locationRepository;

	/**
	 * Текущий отображаемый элемент
	 */
	@Getter
	@Setter
	private BuildingElementDto currentElement;

	/**
	 * Рутовый элемент
	 */
	@Getter
	@Setter
	private BuildingElementDto rootElement;

	/**
	 * Допустимые типы элементов строений
	 */
	@Getter
	private List<BuildingElementTypeDto> possibleTypes;

	/**
	 * Набор адресов помещений, входящих в указанное строение
	 */
	@Getter
	private List<Lodging> possibleLodgings = new ArrayList<>();

	/**
	 * Инициализация
	 */
	@PostConstruct
	public void initialize() {
		possibleTypes = typeService.findAllElementTypes();
	}

	/**
	 * Обработка перед выводом страницы
	 *
	 * @param element
	 * @param rootElement
	 */
	public void preRender(BuildingElementDto element, BuildingElementDto rootElement) {
		this.currentElement = element;
		this.rootElement = rootElement;

		// если текущий элемент не рутовый, а так же его тип подразумевает наличие адреса
		// то запрашиваем возможные адреса помещений
		if (currentElement != rootElement && currentElement.getType().getLevel() != null) {
			possibleLodgings = locationRepository.findAllLodgingsByBuilding(rootElement.getLocation());
		} else {
			// иначе очищаем список с помещениями в данном строении
			possibleLodgings.clear();
		}
	}

	/**
	 * Ищет незанятые адреса на строении
	 */
	public List<Lodging> getFreeLodgings() {
		return service.getFreeLodgings(possibleLodgings, rootElement);
	}

	/**
	 * Отображать ли поле с адресом
	 *
	 * @return истина, если отображать, иначе ложь
	 */
	public Boolean isAddressVisible() {
		return currentElement.getType().getLevel() != null;
	}

	/**
	 * Изменить имя и тип элемента
	 */
	public void changeNameAndType() {
		currentElement = service.changeNameAndType(currentElement.getId(), currentElement.getName(), currentElement.getType().getId());
		if (currentElement.getType().getLevel() == null) {
			currentElement.setLocation(null);
			currentElement = service.changeLocation(currentElement.getId(), currentElement.getLocation());
		}
	}

	/**
	 * Изменить расположение элемента
	 */
	public void changeLocation() {
		currentElement = service.changeLocation(currentElement.getId(), currentElement.getLocation());
	}
}
