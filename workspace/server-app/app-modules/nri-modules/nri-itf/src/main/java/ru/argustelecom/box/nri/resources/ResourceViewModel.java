package ru.argustelecom.box.nri.resources;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceAppService;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDtoTranslator;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.getLast;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.TreeUtils.sortTree;

/**
 * Модель страницы карточки ресурса
 * Created by s.kolyada on 22.09.2017.
 */
@Named(value = "resourceVM")
@PresentationModel
public class ResourceViewModel extends ViewModel {

	private static final long serialVersionUID = -9145670817894282997L;

	/**
	 * Текущий отображаемый ресурс
	 */
	@Getter
	private ResourceInstanceDto currentResource;

	/**
	 * дерево структуры ресурса
	 */
	@Getter
	private TreeNode elementsTree;

	/**
	 * Выбранный элемент
	 */
	@Getter
	@Setter
	private TreeNode selectedNode;

	/**
	 * Выбранный ресурс
	 */
	@Getter
	@Setter
	private ResourceInstanceDto selectedResource;

	/**
	 * Идентифтикатор спецификации, по которой создаём вложенный ресурс
	 */
	@Getter
	@Setter
	private Long newElemSpecificationId;

	/**
	 * Создание ресурса
	 */
	@Getter
	private final Callback<List<ResourceInstanceDto>> createResource = createdResources -> {
		Optional.ofNullable(selectedNode).ifPresent(node -> node.setSelected(false));
		List<DefaultTreeNode> createdTreeNodes = createdResources.stream().map(res -> new DefaultTreeNode(res, selectedNode)).collect(toList());
		selectedNode = select(getLast(createdTreeNodes, null));
	};

	/**
	 * Состояние вьюхи
	 */
	@Inject
	private ResourceViewState viewState;

	/**
	 * Транслятор ресурса в ДТО
	 */
	@Inject
	private ResourceInstanceDtoTranslator resourceTranslator;

	/**
	 * Сервис для работы с ресурсами
	 */
	@Inject
	private ResourceInstanceAppService service;

	/**
	 * Действия после созданя модели
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		init();

		unitOfWork.makePermaLong();
	}
	public PortType[] getPortTypes() {
		return PortType.values();
	}
	/**
	 * Инициализация вьюхи
	 */
	public void init() {
		ResourceInstance resourceInstance = viewState.getResource();

		ResourceInstance rootResource = resourceInstance;
		if (rootResource != null) {
			while (rootResource.getParent() != null) {
				rootResource = rootResource.getParent();
			}
		}

		currentResource = resourceTranslator.translate(resourceInstance);
		ResourceInstanceDto rootResourceDto = resourceTranslator.translate(rootResource);

		elementsTree = initTree(rootResourceDto);
		if (selectedNode != null) {
			selectedNode.setSelected(true);
			selectedResource = (ResourceInstanceDto) selectedNode.getData();
		}

		sortTree(elementsTree);
	}

	/**
	 * Инициализация дерева элементов из дто объектов
	 *
	 * @param rootElement корневой элемент структуры
	 * @return дерево из элементов структуры
	 */
	private TreeNode initTree(ResourceInstanceDto rootElement) {
		TreeNode result = new DefaultTreeNode("рут структуры", null);
		if (rootElement != null) {
			TreeNode rootNode = convertElementToNode(result, rootElement);
			rootNode.setExpanded(true);
		}
		return result;
	}

	/**
	 * Преобразовать элемент в описатель узла
	 *
	 * @param parentNode родительский узел
	 * @param elementDto элемент структуры
	 */
	private TreeNode convertElementToNode(TreeNode parentNode, ResourceInstanceDto elementDto) {
		TreeNode currentNode = new DefaultTreeNode(elementDto, parentNode);
		for (ResourceInstanceDto childDto : elementDto.getChildren()) {
			convertElementToNode(currentNode, childDto);
		}
		if (currentResource.getId().equals(elementDto.getId())) {
			selectedNode = currentNode;
		}
		return currentNode;
	}

	/**
	 * Событие выделения элемента
	 *
	 * @param event событие выделения элемента
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		selectedResource = (ResourceInstanceDto) event.getTreeNode().getData();
	}

	/**
	 * Очистить параметры создания
	 */
	public void cleanCreationParams() {
		newElemSpecificationId = null;
	}

	/**
	 * Удалить выбранный ресурс
	 */
	public void removeSelectedResource() {
		if (selectedResource != null) {
			service.removeResource(selectedResource.getId());
			TreeNode parent = selectedNode.getParent();
			if (parent != null && !parent.equals(elementsTree)) {
				List<TreeNode> nodeNeighbors = parent.getChildren();
				nodeNeighbors.remove(selectedNode);
				selectedNode = select(getFirst(nodeNeighbors, parent));
			} else {
				selectedResource = null;
				selectedNode = null;
			}
		}
	}

	/**
	 * Выбрать ноду и ресурс
	 *
	 * @param treeNode нода для выбора
	 * @return выбранная нода
	 */
	private TreeNode select(TreeNode treeNode) {
		if (treeNode != null) {
			selectedResource = (ResourceInstanceDto) treeNode.getData();
			treeNode.setSelected(true);
		} else {
			selectedResource = null;
		}
		return treeNode;
	}

	/**
	 * Проверяем можно ли удалить ресурс
	 *
	 * @return true если можно удалять
	 */
	public boolean isRemovableResource() {
		//самый главный это "рут структуры" под ним рутовый ресурс его удалять нельзя
		//Проверять на то что данные instanceof ResourceInstanceDto думаю смысля мало
		return selectedNode != null && selectedNode.getData() != null && selectedNode.getParent() != null &&
				selectedNode.getParent().getParent() != null;
	}

	/**
	 * Может ли выбранный ресурс содержать номера
	 *
	 * @return true, если может
	 */
	public boolean resourceCanContainLogicalResources() {
		return selectedResource != null &&
				selectedResource.getSpecification() != null &&
				CollectionUtils.isNotEmpty(selectedResource.getSpecification().getSupportedLogicalResources());
	}
}
