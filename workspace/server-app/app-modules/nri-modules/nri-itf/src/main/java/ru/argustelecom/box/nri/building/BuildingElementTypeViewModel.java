package ru.argustelecom.box.nri.building;

import lombok.Getter;
import lombok.Setter;
import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import ru.argustelecom.box.env.address.LocationLevelRepository;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.building.nls.BuildingElementTypeVMMessagesBundle;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Модель представления типов элементов строений
 * Created by s.kolyada on 23.08.2017.
 */
@Named(value = "buildingElementTypeVM")
@PresentationModel
public class BuildingElementTypeViewModel extends ViewModel {

	private static final Logger log = Logger.getLogger(BuildingElementTypeViewModel.class);

	/**
	 * Репозиторий доступа к типам
	 */
	@Inject
	private BuildingElementTypeAppService appService;

	/**
	 * Репозиротий доступа к хранилищу уровней модели адресов
	 */
	@Inject
	private LocationLevelRepository levelRepository;

	/**
	 * Сервис для операций над элементами строений
	 */
	@Inject
	private BuildingElementAppService buildingElementService;

	/**
	 * Все доступные типы
	 */
	@Getter
	private List<BuildingElementTypeDto> allTypes;

	/**
	 * Список выбранных типов
	 */
	@Getter
	@Setter
	private BuildingElementTypeDto selectedType;

	/**
	 * Новый тип
	 */
	@Getter
	@Setter
	private BuildingElementTypeDto newType = new BuildingElementTypeDto();

	/**
	 * Список типов одного уровня
	 */
	@Getter
	private List<BuildingElementTypeDto> sameLevelTypes;

	/**
	 * Тип, с которого перемещаются элементы строения
	 */
	@Getter
	@Setter
	private BuildingElementTypeDto moveFrom;

	/**
	 * Тип, на который перемещаются элементы строения
	 */
	@Getter
	@Setter
	private BuildingElementTypeDto moveTo;

	/**
	 * Инициализация
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	/**
	 * Получить из хранилища все типы
	 *
	 * @return Список типов элементов строений
	 */
	public List<BuildingElementTypeDto> getTypes() {
		if (allTypes == null) {
			allTypes = appService.findAllElementTypes();
		}
		return allTypes;
	}

	/**
	 * Создать новый тип
	 */
	public void create() {
		BuildingElementTypeDto buildingElementType
				= appService.createElementType(newType.getName(), newType.getLevel(), newType.getIcon());
		allTypes.add(buildingElementType);
		allTypes.sort(Comparator.comparing(BuildingElementTypeDto::getName));
		cleanCreationParams();
	}

	/**
	 * Обновить существующий тип
	 *
	 * @param event Событие изменения строки с типом
	 */
	public void onRowEdit(RowEditEvent event) {
		BuildingElementTypeDto dto = (BuildingElementTypeDto) event.getObject();
		appService.updateElementType(dto.getId(), dto.getName(), dto.getIcon());
	}

	/**
	 * Удалить выбранные типы
	 */
	public void removeSelectedType() {
		try {
			appService.removeElementType(selectedType.getId());
			allTypes.remove(selectedType);
			selectedType = null;
		} catch (BusinessExceptionWithoutRollback e) {
			log.warn("Ошибка", e);
			sameLevelTypes = appService.findAllByLevel(selectedType.getLevel());
			sameLevelTypes.remove(selectedType);
			if (isEmpty(sameLevelTypes)) {

				Notification.error(LocaleUtils.getMessages(BuildingElementTypeVMMessagesBundle.class).error(), e.getMessage());
			} else {
				moveFrom = selectedType;
				RequestContext.getCurrentInstance().execute("PF('moveBuildingElementsDlg').show();");
				RequestContext.getCurrentInstance().update("move_building_elements_dlg");
			}
		}
	}

	/**
	 * Изменить тип в элементах строения и удалить выбранный
	 */
	public void moveBuildingElements() {
		buildingElementService.changeType(moveFrom.getId(), moveTo.getId());
		allTypes.remove(moveFrom);
		appService.remove(moveFrom.getId());
		sameLevelTypes = null;
	}

	/**
	 * Очистить параметры создания
	 */
	public void cleanCreationParams() {
		newType = new BuildingElementTypeDto();
	}

	/**
	 * Валидация имени
	 *
	 * @param facesContext facesContext
	 * @param component    UIComponent
	 * @param objectName   Имя для валидации
	 */
	@SuppressWarnings("unused")
	public void nameValidator(FacesContext facesContext, UIComponent component, Object objectName) {
		if (objectName == null)
			return;
		String objName = ((String) objectName).trim();
		if (objName.isEmpty())
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, LocaleUtils.getMessages(BuildingElementTypeVMMessagesBundle.class).nameCanNotBeEmpty(), ""));

		for (BuildingElementTypeDto type : allTypes)
			if (objName.equalsIgnoreCase(type.getName()) && !type.equals(selectedType))
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, LocaleUtils.getMessages(BuildingElementTypeVMMessagesBundle.class).nameDoesNotUnique(), ""));
	}

	public List<LocationLevel> getLevels() {
		return Arrays.asList(levelRepository.building(), levelRepository.lodging());
	}

	public BuildingElementTypeIcon[] getIcons() {
		return BuildingElementTypeIcon.values();
	}
}
