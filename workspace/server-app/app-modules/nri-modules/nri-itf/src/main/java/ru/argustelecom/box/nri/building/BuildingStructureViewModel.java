package ru.argustelecom.box.nri.building;

import lombok.Getter;
import lombok.Setter;
import org.jboss.logging.Logger;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.box.env.address.LocationRepository;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.building.nls.BuildingStructureViewModelMessagesBundle;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.box.nri.service.ServiceSpecificationRepository;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static ru.argustelecom.box.TreeUtils.sortTree;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

/**
 * Модель представления структуры дома
 * Created by s.kolyada on 25.08.2017.
 */
@Named(value = "buildingStructureVM")
@PresentationModel
public class BuildingStructureViewModel extends ViewModel {

	private static final long serialVersionUID = -9145670817894282997L;

	private static final Logger log = Logger.getLogger(BuildingStructureViewModel.class);

	/**
	 * Репозиторий доступа к адресным данным
	 */
	@Inject
	private LocationRepository locationRepository;

	/**
	 * Сервис работы с элементами строения
	 */
	@Inject
	private BuildingElementAppService elementService;

	/**
	 * Состояние вьюхи
	 */
	@Inject
	private BuildingStructureViewState viewState;

	/**
	 * Сервис доступа к харанилищу дто
	 */
	@Inject
	private BuildingElementTypeAppService dtoService;

	@Inject
	private ServiceSpecificationRepository specificationRepository;

	/**
	 * Рутовый элемент строения
	 */
	@Getter
	@Setter
	private BuildingElementDto rootElement;

	/**
	 * Адрес строения для отображения в заголовке вкладки браузера
	 */
	@Getter
	private Building buildingLocation;

	/**
	 * дерево элементов строения
	 */
	@Getter
	@Setter
	private TreeNode elementsTree;

	/**
	 * Выбранный элемент
	 */
	@Getter
	@Setter
	private TreeNode selectedNode;

	/**
	 * Создаваемый элемент строения
	 */
	@Getter
	@Setter
	private BuildingElementDto newElement = new BuildingElementDto();

	/**
	 * Выбранный тип для элементов создаваемых по умолчанию
	 */
	@Getter
	@Setter
	private BuildingElementTypeDto typeForAll = new BuildingElementTypeDto();

	/**
	 * Выбранный тип для элементов создаваемых по умолчанию
	 */
	@Getter
	@Setter
	private BuildingElementTypeDto typeForBuilding = new BuildingElementTypeDto();

	/**
	 * Набор адресов помещений, входящих в указанное строение
	 */
	@Getter
	private List<Lodging> possibleLodgings = new ArrayList<>();

	/**
	 * Инициализация
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		buildingLocation = findLocation(viewState);
		rootElement = elementService.findElementByLocation(buildingLocation);
		elementsTree = initTree(rootElement);
		sortTree(elementsTree);
		expandTreeToElement(elementsTree, viewState.getBuildingElement());

		// загрузить список всех входящих в строение помещений
		possibleLodgings = locationRepository.findAllLodgingsByBuilding(buildingLocation);
		unitOfWork.makePermaLong();
	}

	/**
	 * ищет адрес строения в состоянии вьюхи
	 *
	 * @param viewState состояние вьюхи
	 * @return адрес строения
	 * @throws BusinessException если не смог найти адрес
	 */
	private Building findLocation(BuildingStructureViewState viewState) {
		BuildingElement currentElement = viewState.getBuildingElement();
		Location location = null;

		// если передан эремент, который надо раскрыть на дереве, ищем отца и раскрываем
		if (currentElement != null) {
			BuildingElement oldestParent = getOldestParent(currentElement);
			location = oldestParent.getLocation();
		} else if (viewState.getLocation() != null)
			location = viewState.getLocation();

		location = initializeAndUnproxy(location);
		if (location == null || !(location instanceof Building))
			throw new BusinessException(LocaleUtils.getMessages(BuildingStructureViewModelMessagesBundle.class)
					.thereIsLackOfAddressInformationToShowBuildingStructure());
		return (Building) location;
	}

	/**
	 * ищет рутового отца
	 *
	 * @param element элемент, чьего отца будем искать
	 * @return найденный отец. либо элемент, если отца у него нет
	 */
	private BuildingElement getOldestParent(@NotNull BuildingElement element) {
		return element.getParent() == null ? element : getOldestParent(element.getParent());
	}

	/**
	 * Раскрывает дерево до узла с требуемым элементом строения
	 *
	 * @param tree    дерево, которое будем раскрывать
	 * @param element элемент до которого будем раскрывать. если null, то раскрываем рут
	 */
	private void expandTreeToElement(TreeNode tree, BuildingElement element) {
		if (element == null) {
			tree.getChildren().forEach(node -> node.setSelected(true));
			return;
		}

		TreeNode elementNode = flatten(tree)
				.filter(node -> node.getData() instanceof BuildingElementDto)
				.filter(node -> element.getId().equals(((BuildingElementDto) node.getData()).getId()))
				.findFirst().orElse(null);
		if (elementNode != null) {
			elementNode.setSelected(true);
			selectedNode = elementNode;
		}

		while (elementNode != null) {
			elementNode.setExpanded(true);
			elementNode = elementNode.getParent();
		}
	}

	/**
	 * возвращает список свободных адресов
	 *
	 * @return список свободных адресов
	 */
	public List<Lodging> getFreeLodgings() {
		return elementService.getFreeLodgings(possibleLodgings, rootElement);
	}

	/**
	 * Стрим из элементов дерева
	 *
	 * @param tree дерево
	 * @return стрим из элементов дерева
	 */
	private Stream<TreeNode> flatten(TreeNode tree) {
		return Stream.concat(Stream.of(tree), tree.getChildren().stream().flatMap(this::flatten));
	}

	/**
	 * Проинициализировать модель данные из адресного справочника
	 * создав структуру поумолчанию
	 */
	public void initDefaultStructure() {
		// получаем расположение элемента из запроса
		Location location = viewState.getLocation();

		if (rootElement == null) {
			BuildingElementTypeDto type = typeForBuilding;

			BuildingStructureViewModelMessagesBundle messages = LocaleUtils.getMessages(BuildingStructureViewModelMessagesBundle.class);
			//Запихиваем все возможные помещения в строение
			rootElement = BuildingElementDto.builder()
					.name(messages.building())
					.location(location)
					.type(type)
					.childElements(possibleLodgings.stream()
							.map(lodging -> BuildingElementDto.builder()
									.name(lodging.getType().getShortName() + " " + lodging.getNumber())
									.location(lodging)
									.childElements(new ArrayList<>())
									.type(typeForAll).build())
							.collect(toList()))
					.build();

			rootElement = elementService.createNewElementWithChildren(rootElement, null);
		}

		elementsTree = initTree(rootElement);
		expandTreeToElement(elementsTree, null);
	}

	/**
	 * Инициализация дерева элементов из дто объектов
	 *
	 * @param rootElement корневой элемент структуры
	 * @return дерево из элементов структуры
	 */
	private TreeNode initTree(BuildingElementDto rootElement) {
		TreeNode result = new DefaultTreeNode("рут строения", null);
		if (rootElement != null) {
			TreeNode rootNode = convertElementToNode(result, rootElement);
			rootNode.setExpanded(true);
			selectedNode = rootNode;
		}
		return result;
	}

	/**
	 * Преобразовать элементв описатель узла
	 *
	 * @param parentNode родительский узел
	 * @param elementDto элемент структуры
	 */
	private TreeNode convertElementToNode(TreeNode parentNode, BuildingElementDto elementDto) {
		TreeNode currentNode = new DefaultTreeNode(elementDto, parentNode);
		for (BuildingElementDto childDto : elementDto.getChildElements()) {
			convertElementToNode(currentNode, childDto);
		}
		return currentNode;
	}

	/**
	 * Создать новый элемент
	 */
	public void create() {
		BuildingElementDto selectedElement = (BuildingElementDto) selectedNode.getData();
		newElement = elementService.createNewElement(newElement, selectedElement);
		convertElementToNode(selectedNode, newElement);
		selectedElement.getChildElements().add(newElement);
		cleanCreationParams();
	}

	/**
	 * Удалить элемент
	 */
	public void delete() {
		BuildingElementDto selectedElement = (BuildingElementDto) selectedNode.getData();
		List<ResourceInstallationDto> installations = elementService.getResourceInstallations(selectedElement);
		if (!isEmpty(installations)) {
			BuildingStructureViewModelMessagesBundle messages = LocaleUtils.getMessages(BuildingStructureViewModelMessagesBundle.class);
			Notification.warn(messages.couldNotToDelete() + " " + selectedElement.getName(),
					messages.thisElementContainsMountPoint());
			return;
		}

		TreeNode parentNode = selectedNode.getParent();
		parentNode.getChildren().remove(selectedNode);

		// data отца может быть не BuildingElementDto, когда мы удаляем все здание
		if (parentNode.getData() instanceof BuildingElementDto)
			((BuildingElementDto) parentNode.getData()).getChildElements().remove(selectedElement);

		elementService.delete(selectedElement.getId());
		if (rootElement == selectedElement) {
			rootElement = null;
			selectedNode = null;
		} else {
			selectedNode = parentNode;
			selectedNode.setSelected(true);
		}
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
		String name = ((String) objectName).trim();
		if (name.isEmpty()) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					LocaleUtils.getMessages(BuildingStructureViewModelMessagesBundle.class).nameCanNotBeEmpty(), ""));
		}
	}

	/**
	 * Очистить параметры создания
	 */
	public void cleanCreationParams() {
		newElement = new BuildingElementDto();
		typeForAll = new BuildingElementTypeDto();
	}

	public Boolean isAddressVisible() {
		return newElement.getType() != null && newElement.getType().getLevel() != null;
	}

	/**
	 * Обработчик соьытия изменения структуры дерева элементов
	 *
	 * @param event событие
	 */
	public void onNodeParentChange(TreeDragDropEvent event) {
		TreeNode dragNode = event.getDragNode();
		TreeNode dropNode = event.getDropNode();

		BuildingElementDto changedElement = (BuildingElementDto) dragNode.getData();
		BuildingElementDto parentElement = (BuildingElementDto) dropNode.getData();

		BuildingStructureViewModelMessagesBundle messages = LocaleUtils.getMessages(BuildingStructureViewModelMessagesBundle.class);
		try {
			elementService.changeElementParent(changedElement, parentElement);
			dropNode.setExpanded(true);
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, messages.structureChanging(),
					messages.buildingStructureWasChanged());
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			log.error("Не удалось изменить структуру строения", e);

			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.structureChanging(),
					messages.couldNotChangeBuildingStructure());
			FacesContext.getCurrentInstance().addMessage(null, message);
			// обновляем дерево, тк обработка могла завершиться неудачно, а ui не откатить назад
			elementsTree = initTree(rootElement);
		}
	}

	/**
	 * Получить типы элементов строений
	 *
	 * @return список типов
	 */
	public List<BuildingElementTypeDto> getTypes() {
		return dtoService.findAllElementTypes();
	}

	/**
	 * Получить типы элементов уровня помкщение
	 *
	 * @return список типов
	 */
	public List<BuildingElementTypeDto> getLodgingTypes() {
		return dtoService.findAllElementTypes().stream()
				.filter(t -> t.getLevel() != null && LocationLevel.LODGING.equals(t.getLevel().getId()))
				.collect(toList());
	}

	/**
	 * Получить типы элементов уровня помещение
	 *
	 * @return список типов
	 */
	public List<BuildingElementTypeDto> getBuildingTypes() {
		return dtoService.findAllElementTypes().stream()
				.filter(t -> t.getLevel() != null && LocationLevel.BUILDING.equals(t.getLevel().getId()))
				.collect(toList());
	}

	/**
	 * Явно указывает элементу остаться открытым
	 *
	 * @param event событие открытия элемента
	 */
	public void onNodeExpand(NodeExpandEvent event) {
		event.getTreeNode().setExpanded(true);
	}

	/**
	 * Явно указывает элементу остаться закрытым
	 *
	 * @param event событие закрытия элемента
	 */
	public void onNodeCollapse(NodeCollapseEvent event) {
		event.getTreeNode().setExpanded(false);
	}

	/**
	 * Событие выделения элемента
	 *
	 * @param event событие выделения элемента
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		selectedNode = event.getTreeNode();
	}
}
