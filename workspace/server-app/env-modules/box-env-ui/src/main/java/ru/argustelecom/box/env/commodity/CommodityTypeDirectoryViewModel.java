package ru.argustelecom.box.env.commodity;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import lombok.Getter;
import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.type.CurrentType;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;
import ru.argustelecom.system.inf.tree.LazyTreeNode;

@Named(value = "commodityTypeDirectoryVm")
@PresentationModel
public class CommodityTypeDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = -1644513452899174199L;

	@Inject
	private CommodityTypeDirectoryViewState viewState;

	@Inject
	private CommodityTypeGroupAppService commodityTypeGroupAs;

	@Inject
	private CommodityTypeRepository commodityTypeRp;

	@Inject
	private CommodityTypeTreeNodeDtoTranslator commodityTypeTreeNodeDtoTr;

	@Inject
	private CurrentType currentType;

	private CommodityTypeLazyTreeNodeStrategy strategy;

	private CommodityTypeLazyTreeNodeLoader loader;

	@Getter
	private LazyTreeNode<CommodityTypeTreeNodeDto> treeNode;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		initCurrentType(viewState.getNodeDto());
		initNode(viewState.getNodeDto());
	}

	public void onCreationDialogOpen() {
		RequestContext.getCurrentInstance().execute("PF('categoriesPanel').hide()");
		RequestContext.getCurrentInstance().update("commodity_type_creation_form-commodity_type_creation_dlg");
		RequestContext.getCurrentInstance().execute("PF('commodityTypeCreationDlgVar').show()");
	}

	public void remove() {
		TreeNode parent = strategy.getSelectedNode().getParent();
		parent.getChildren().remove(strategy.getSelectedNode());
		initCurrentType(null);

		if (viewState.getNodeDto().isGroup()) {
			em.remove(em.find(CommodityTypeGroup.class, viewState.getNodeDto().getId()));
		} else {
			em.remove(em.find(CommodityType.class, viewState.getNodeDto().getId()));
		}

		setSelectedNode(null);
		((LazyTreeNode) parent).setLeaf(parent.getChildren().isEmpty());
	}

	public Callback<CommodityTypeTreeNodeDto> getCallbackAfterCreation() {
		return this::addNode;
	}

	public Callback<BusinessObjectDto<CommodityTypeGroup>> getCallbackAfterGroupChanged() {
		return instance -> moveNode(Optional.ofNullable(instance).map(BusinessObjectDto::getId).orElse(null));
	}

	public LazyTreeNode getSelectedNode() {
		return strategy.getSelectedNode();
	}

	public void setSelectedNode(LazyTreeNode selectedNode) {
		strategy.setSelectedNode(selectedNode);
		Optional<LazyTreeNode> selectedNodeOptional = Optional.ofNullable(selectedNode);
		viewState.setNodeDto(selectedNodeOptional.map(node -> (CommodityTypeTreeNodeDto) node.getData()).orElse(null));
		initCurrentType(viewState.getNodeDto());
	}

	private void initCurrentType(CommodityTypeTreeNodeDto selectedNode) {
		if (selectedNode == null || selectedNode.isGroup()) {
			currentType.setValue(null);
		} else {
			currentType.setValue((Type) EntityManagerUtils.initializeAndUnproxy(selectedNode.getIdentifiable()));
		}
	}

	private void addNode(CommodityTypeTreeNodeDto nodeDto) {
		TreeNode parentNode = findNode(nodeDto.getParentId(), treeNode);

		Preconditions.checkNotNull(parentNode);

		List<TreeNode> treeNodes = LazyTreeNode.create(Lists.newArrayList(nodeDto), strategy, loader, parentNode);
		TreeNode newNode = treeNodes.get(0);
		parentNode.getChildren().add(newNode);

		if (strategy.getSelectedNode() != null) {
			strategy.getSelectedNode().setSelected(false);
		}
		newNode.setSelected(true);
		parentNode.setSelected(false);
		markExpanded(parentNode);

		initCurrentType(nodeDto);
		setSelectedNode((LazyTreeNode) newNode);
	}

	@SuppressWarnings("unchecked")
	private void markExpanded(TreeNode node) {
		LazyTreeNode<CommodityTypeTreeNodeDto> lazyTreeNode = (LazyTreeNode<CommodityTypeTreeNodeDto>) node;
		lazyTreeNode.setExpanded(true);
		lazyTreeNode.setLeaf(lazyTreeNode.getChildren().isEmpty());
	}

	private List<CommodityTypeTreeNodeDto> loadRootNodes() {
		return commodityTypeRp.findRootGroups().stream().map(commodityTypeTreeNodeDtoTr::translate)
				.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private Set<CommodityTypeTreeNodeDto> getParents(CommodityTypeTreeNodeDto node) {
		return translateGroups(commodityTypeGroupAs.findParentGroups(node.getParentId()));
	}

	private Set<CommodityTypeTreeNodeDto> translateGroups(List<CommodityTypeGroup> groups) {
		return groups.stream().map(commodityTypeTreeNodeDtoTr::translate).collect(Collectors.toSet());
	}

	@SuppressWarnings("ConstantConditions")
	private void moveNode(Long newGroupId) {
		TreeNode movingNode = strategy.getSelectedNode();
		TreeNode oldParent = movingNode.getParent();
		TreeNode newParent = findNode(newGroupId, treeNode);

		oldParent.getChildren().remove(movingNode);
		newParent.getChildren().add(movingNode);

		movingNode.setParent(newParent);

		newParent.setExpanded(true);
	}

	private TreeNode findNode(Long groupId, TreeNode node) {
		if (groupId == null) {
			return treeNode;
		}

		CommodityTypeTreeNodeDto data = (CommodityTypeTreeNodeDto) node.getData();
		if (data != null && data.isGroup() && Objects.equals(data.getId(), groupId)) {
			return node;
		} else {
			for (TreeNode child : node.getChildren()) {
				TreeNode result = findNode(groupId, child);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	private void initNode(CommodityTypeTreeNodeDto selectedNodeDto) {
		loader = new CommodityTypeLazyTreeNodeLoader(em, commodityTypeRp, commodityTypeTreeNodeDtoTr);
		strategy = new CommodityTypeLazyTreeNodeStrategy();
		if (selectedNodeDto != null) {
			Set<CommodityTypeTreeNodeDto> expandedUnits = new HashSet<>();
			expandedUnits.addAll(getParents(selectedNodeDto));
			expandedUnits.add(selectedNodeDto);
			strategy.setExpandedUnits(expandedUnits);
			strategy.setSelectedUnit(selectedNodeDto);
		}
		treeNode = LazyTreeNode.createRoot(loadRootNodes(), strategy, loader);
	}

}