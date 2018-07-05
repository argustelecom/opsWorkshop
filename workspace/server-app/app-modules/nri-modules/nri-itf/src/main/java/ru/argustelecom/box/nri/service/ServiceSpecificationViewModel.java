package ru.argustelecom.box.nri.service;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.nri.resources.requirements.RequiredItemAppService;
import ru.argustelecom.box.nri.resources.requirements.RequiredItemDto;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaAppService;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationAppService;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.TreeUtils.sortTree;

/**
 * Модель страницы карточки ресурса
 * Created by b.bazarov on 05.10.2017.
 */
@Named(value = "serviceSpecificationVM")
@PresentationModel
public class ServiceSpecificationViewModel extends ViewModel {

	private static final long serialVersionUID = 6994234089335693389L;
	/**
	 * Текущая спецификация услуги
	 */
	@Getter
	private ServiceSpec currentServiceSpecification;

	/**
	 * Имя новой схемы
	 */
	@Getter
	@Setter
	private String newSchemaName = "";

	/**
	 * Сервис для работы со схемами требований к ресурсам
	 */
	@Inject
	private ResourceSchemaAppService serviceResourceSchema;

	/**
	 * Выбранный элемент
	 */
	@Getter
	@Setter
	private TreeNode selectedNode;

	/**
	 * Идентифтикатор спецификации, по которой создаём требование
	 */
	@Getter
	@Setter
	private Long newElemSpecificationId;

	/**
	 * Список спецификаций ресурсов
	 */
	@Setter
	private List<ResourceSpecificationDto> possibleResourceSpecification = new ArrayList<>();

	/**
	 * Состояние вьюхи
	 */
	@Inject
	private ServiceSpecificationViewState viewState;

	/**
	 * Сервис для работы с требованиями
	 */
	@Inject
	private RequiredItemAppService requiredItemAppService;

	/**
	 * Сервис спецификаций ресурсов
	 */
	@Inject
	private ResourceSpecificationAppService resourceSpecificationService;

	/**
	 * дерево элементов строения
	 */
	@Getter
	@Setter
	private TreeNode elementsTree;

	/**
	 * здесь хранятся все схемы
	 */
	private List<ResourceSchemaDto> allSchemas;

	/**
	 * Действия после создания модели
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		init();

		unitOfWork.makePermaLong();
	}

	/**
	 * Инициализация представления
	 */
	public void init() {
		currentServiceSpecification = viewState.getSpecification();
		allSchemas = serviceResourceSchema.findAll(currentServiceSpecification.getId());
		elementsTree = initTree(allSchemas);
		sortTree(elementsTree);
		possibleResourceSpecification = resourceSpecificationService.findAllSpecifications();
		if (!CollectionUtils.isEmpty(elementsTree.getChildren())) {
			selectedNode = elementsTree.getChildren().get(0);
			selectedNode.setSelected(true);
			selectedNode.setExpanded(true);
		} else
			selectedNode = null;
	}

	/**
	 * Получить список спецификаций ресурсов для создания требования к ресурсу
	 *
	 * @return список спецификаций русерса
	 */
	public List<ResourceSpecificationDto> getPossibleResourceSpecification() {
		if (selectedNode != null) {
			Object node = selectedNode.getData();
			if (node instanceof RequiredItemDto)
				return possibleResourceSpecification.stream()
						.filter(spec -> ((RequiredItemDto) node).getResourceSpecification().equals(spec))
						.flatMap(spec -> spec.getChildSpecifications().stream())
						.collect(toList());
			else
				return possibleResourceSpecification;
		} else
			return emptyList();
	}

	/**
	 * Инициализация дерева
	 *
	 * @param list список схем
	 * @return Коренная нода
	 */
	private TreeNode initTree(List<ResourceSchemaDto> list) {
		TreeNode result = new DefaultTreeNode("рут схем", null);
		if (CollectionUtils.isEmpty(list))
			return result;
		for (ResourceSchemaDto schema : list) {
			if (schema != null) {
				TreeNode rootNode = convertElementToNode(result, schema);
				rootNode.setExpanded(true);
			}
		}
		return result;
	}

	/**
	 * Очистить параметры создания новой схемы
	 */
	public void cleanSchemaCreationParams() {
		newSchemaName = "";
		newElemSpecificationId = null;
		sortTree(elementsTree);
	}

	/**
	 * создать новую схему
	 */
	public void createSchema() {
		if (!StringUtils.isBlank(newSchemaName)) {
			Optional.ofNullable(selectedNode).ifPresent(node -> {
				node.setSelected(false);
				node.setExpanded(true);
			});
			ResourceSchemaDto newSchema = serviceResourceSchema.createResourceSchema(newSchemaName, currentServiceSpecification.getId());
			allSchemas.add(newSchema);
			elementsTree = initTree(allSchemas);
			selectedNode = findNode(elementsTree, newSchema);
			if (selectedNode != null)
				selectedNode.setSelected(true);
		}
		cleanSchemaCreationParams();
	}

	/**
	 * Создать требование
	 */
	public void createRequirement() {

		if (newElemSpecificationId != null && selectedNode != null) {
			Object o = selectedNode.getData();
			selectedNode.setSelected(false);
			selectedNode.setExpanded(true);
			RequiredItemDto newRequirement;
			if (o instanceof RequiredItemDto) {
				newRequirement = requiredItemAppService.create(newElemSpecificationId, (RequiredItemDto) o);
				((RequiredItemDto) o).getChildren().add(newRequirement);
			} else if (o instanceof ResourceSchemaDto) {
				newRequirement = requiredItemAppService.create(newElemSpecificationId, (ResourceSchemaDto) o);
				((ResourceSchemaDto) o).getRequirements().add(newRequirement);
			} else {
				newRequirement = null;
			}
			elementsTree = initTree(allSchemas);
			selectedNode = findNode(elementsTree, newRequirement);
			if (selectedNode != null)
				selectedNode.setSelected(true);
		}
		cleanSchemaCreationParams();
	}

	/**
	 * Ищем ноду по данным
	 *
	 * @param startNode Начальная нода
	 * @param o         данные
	 * @return Нода с данными
	 */
	private TreeNode findNode(TreeNode startNode, Object o) {
		if (o == null || startNode == null)
			return null;
		if (o.equals(startNode.getData()))
			return startNode;
		TreeNode result = null;
		for (TreeNode node : startNode.getChildren()) {
			result = findNode(node, o);
			if (result != null)
				return result;
		}
		return result;
	}

	/**
	 * Событие выделения элемента
	 *
	 * @param event событие выделения элемента
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		selectedNode = event.getTreeNode();
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
	 * Преобразовать требование в описатель узла
	 *
	 * @param parentNode родительский узел
	 * @param elementDto элемент структуры
	 */
	private TreeNode convertElementToNode(TreeNode parentNode, RequiredItemDto elementDto) {
		TreeNode currentNode = new DefaultTreeNode(elementDto, parentNode);
		for (RequiredItemDto childDto : elementDto.getChildren()) {
			convertElementToNode(currentNode, childDto);
		}
		currentNode.setExpanded(true);
		return currentNode;
	}

	/**
	 * Преобразовать требование в описатель узла
	 *
	 * @param parentNode родительский узел
	 * @param elementDto элемент структуры
	 */
	private TreeNode convertElementToNode(TreeNode parentNode, ResourceSchemaDto elementDto) {
		TreeNode currentNode = new DefaultTreeNode(elementDto, parentNode);
		for (RequiredItemDto childDto : elementDto.getRequirements()) {
			convertElementToNode(currentNode, childDto);
		}
		currentNode.setExpanded(true);
		return currentNode;
	}

	/**
	 * Удаление выбранного элемента
	 */
	public void removeSelectedItem() {
		//определим что кликнули
		if (selectedNode != null) {
			Object o = selectedNode.getData();

			TreeNode parent = selectedNode.getParent();
			Object parentObject = parent != null ? parent.getData() : null;
			deleteNode(selectedNode);
			if (o instanceof RequiredItemDto) {
				requiredItemAppService.removeItem(((RequiredItemDto) o).getId());
				if (parentObject != null && parentObject instanceof ResourceSchemaDto)
					((ResourceSchemaDto) parentObject).getRequirements().remove(o);
				else if (parentObject != null && parentObject instanceof RequiredItemDto)
					((RequiredItemDto) parentObject).getChildren().remove(o);
			} else if (o instanceof ResourceSchemaDto) {
				allSchemas.remove(o);
				serviceResourceSchema.removeResourceSchema(((ResourceSchemaDto) o).getId());
			}
			elementsTree = initTree(allSchemas);
			sortTree(elementsTree);
			selectedNode.setSelected(false);
			selectedNode = Optional.ofNullable(parent)
					.filter(node -> !node.equals(elementsTree))
					.orElse(elementsTree.getChildren().stream().findAny().orElse(null));
			if (selectedNode != null) {
				selectedNode.setSelected(true);
			}
		}
	}

	/**
	 * Удалить узел дерева
	 *
	 * @param node удаляемый узел
	 */
	private void deleteNode(TreeNode node) {
		if (node.getParent() != null) {
			node.getParent().getChildren().remove(node);
		}
	}

	/**
	 * Выбранное требование
	 *
	 * @return Выбранное требование
	 */
	public RequiredItemDto getSelectedRequiredItemDto() {
		return selectedNode != null && selectedNode.getData() instanceof RequiredItemDto ? (RequiredItemDto) selectedNode.getData() : null;
	}

	/**
	 * Выбранная схема
	 *
	 * @return Выбранная схема
	 */
	public ResourceSchemaDto getSelectedSchemaDto() {
		return selectedNode != null && selectedNode.getData() instanceof ResourceSchemaDto ? (ResourceSchemaDto) selectedNode.getData() : null;
	}

	/**
	 * Сохранить изменение в имени
	 */
	public void changeSchemeName() {
		ResourceSchemaDto dto = getSelectedSchemaDto();
		if (getSelectedSchemaDto() != null) {
			serviceResourceSchema.changeName(dto);
		}
	}

}
