package ru.argustelecom.box.nri.logicalresources.ip.subnet;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.logicalresources.nls.LogicalResourcesMessagesBundle;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import static com.google.common.collect.Iterables.getFirst;
import static ru.argustelecom.box.TreeUtils.sortTree;

/**
 * Контроллер страницы с деревом подсетей
 *
 * @author d.khekk
 * @since 13.12.2017
 */
@PresentationModel
@Named(value = "ipSubnetVM")
public class IPSubnetViewModel extends ViewModel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(IPSubnetViewModel.class);

	/**
	 * Состояние вьюхи
	 */
	@Inject
	private IPSubnetViewState viewState;

	/**
	 * Сервис для операций над подсетями
	 */
	@Inject
	private IPSubnetAppService service;

	/**
	 * Выбранная подсеть
	 */
	@Getter
	@Setter
	private IPSubnetDto selectedSubnet;

	/**
	 * Корневой элемент дерева
	 */
	@Getter
	@Setter
	private TreeNode rootElement;

	/**
	 * Выбранный элемент дерева
	 */
	@Getter
	@Setter
	private TreeNode selectedNode;

	/**
	 * Создать подсеть
	 */
	@Getter
	private BiConsumer<IPSubnetDto, Boolean> createSubnet = (subnetToCreate, isStatic) -> {
		Long id;
		try {
			IPSubnetDto subnet = service.createIpv4Subnet(subnetToCreate, isStatic);
			id = subnet.getId();
		} catch (SubnetAlreadyExistException ex) {
			id = ex.getExistSubnetId();
			log.debug("Попытка создать уже сущестующую подсеть", ex);
			Notification.error(LocaleUtils.getMessages(LogicalResourcesMessagesBundle.class).error(), ex.getLocalizedMessage());
		}
		rootElement = initTree();
		selectedNode = select(findNode(rootElement, id));
		expandAllAboveSelectedNode();
	};

	/**
	 * Действия после открытия вьюхи
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		rootElement = initTree();
		expandAllAboveSelectedNode();
		unitOfWork.makePermaLong();
	}

	/**
	 * Нажатие на кнопку удаления при удалении
	 */
	public void onDelete() {
		if (selectedNode != null) {
			IPSubnetDto subnet = (IPSubnetDto) selectedNode.getData();
			if (subnet.getParentId() != null) {
				removeSubnet(false);
			} else {
				if (CollectionUtils.isEmpty(subnet.getChildSubnets())) {
					removeSubnet(false);
				} else {
					RequestContext.getCurrentInstance().execute("PF('ipSubnetDeletingDlg').show()");
				}
			}
		}
	}

	/**
	 * Найти элемент в дереве с нужным ID
	 *
	 * @param parent родительский элемент
	 * @param id     искомый ID
	 * @return нужный элемент или null
	 */
	private TreeNode findNode(TreeNode parent, Long id) {
		TreeNode found = null;
		Iterator<TreeNode> iterator = parent.getChildren().iterator();
		while (iterator.hasNext() && found == null) {
			TreeNode node = iterator.next();
			if (((IPSubnetDto) node.getData()).getId().equals(id)) {
				found = node;
			} else {
				found = findNode(node, id);
			}
		}
		return found;
	}

	/**
	 * Проинициализировать дерево
	 *
	 * @return проинициализированный рутовый элемент
	 */
	private TreeNode initTree() {
		TreeNode root = new DefaultTreeNode("рут", null);
		List<IPSubnetDto> parentSubnets = service.findParentSubnets();
		parentSubnets.forEach(subnet -> convertElementToNode(root, subnet));
		if (selectedNode == null) {
			selectedNode = select(getFirst(root.getChildren(), null));
		}
		sortTree(root);
		return root;
	}

	/**
	 * Создать элемент дерева из подсети
	 *
	 * @param parentNode родительский элемент
	 * @param subnetDto  подсеть
	 */
	private void convertElementToNode(TreeNode parentNode, IPSubnetDto subnetDto) {
		TreeNode currentNode = new DefaultTreeNode(subnetDto, parentNode);
		if (viewState.getIpSubnet() != null && subnetDto.getId().equals(viewState.getIpSubnet().getId())) {
			selectedNode = select(currentNode);
		}
		for (IPSubnetDto childSubnet : subnetDto.getChildSubnets()) {
			convertElementToNode(currentNode, childSubnet);
		}
	}

	/**
	 * Развернуть все элементы над выбранной нодой
	 */
	private void expandAllAboveSelectedNode() {
		if (selectedNode != null) {
			TreeNode parent = selectedNode.getParent();
			while (!parent.equals(rootElement)) {
				parent.setExpanded(true);
				parent = parent.getParent();
			}
		}
	}


	/**
	 * Произвести действия, требуемые при выборе ноды
	 *
	 * @param treeNode выбранная нода
	 * @return выбранная нода
	 */
	private TreeNode select(TreeNode treeNode) {
		if (treeNode != null) {
			if (treeNode.getData() instanceof IPSubnetDto) {
				selectedSubnet = (IPSubnetDto) treeNode.getData();
			}
			treeNode.setSelected(true);
		} else {
			selectedSubnet = null;
		}
		return treeNode;
	}

	/**
	 * Удалит только подсеть  и входяие в неё адреса
	 */
	public void removeSubnetAndAllChildren() {
		removeSubnet(true);
	}


	/**
	 * удалить подсеть со всеми входящими в нее подсетями и ip-адресами
	 */
	public void removeSubnet() {
		removeSubnet(false);
	}

	/**
	 * Удалить подсеть
	 *
	 * @param removeAll удалять всё дерево или нет
	 */
	private void removeSubnet(boolean removeAll) {
		if (selectedNode != null) {
			IPSubnetDto subnet = (IPSubnetDto) selectedNode.getData();
			try {
				service.deleteIPv4Subnet(subnet.getId(), removeAll);
				rootElement = initTree();
				selectedNode = select(getFirst(rootElement.getChildren(), null));
				viewState.setIpSubnet(null);
			} catch (Exception e) {
				log.error("Ошибка при удалении подсети", e);
				Notification.error(LocaleUtils.getMessages(LogicalResourcesMessagesBundle.class).error(), e.getMessage());
			}
		}
	}

	/**
	 * Действия после выбора элемента
	 *
	 * @param event событие открытия элемента
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		select(event.getTreeNode());
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
}
