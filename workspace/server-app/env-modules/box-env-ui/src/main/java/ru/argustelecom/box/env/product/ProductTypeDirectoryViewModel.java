package ru.argustelecom.box.env.product;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;
import ru.argustelecom.system.inf.tree.LazyTreeNode;

@Named(value = "productTypeDirectoryVm")
@PresentationModel
public class ProductTypeDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = -5173252260332571091L;

	@Inject
	private ProductTypeRepository productTypeRepository;

	@Inject
	private ProductTypeDirectoryViewState productTypeDirectoryViewState;

	@Inject
	private ProductTypeCreationDialogModel productTypeCreationDialogModel;

	private ProductTypeLazyTreeNodeStrategy productTypeLazyTreeNodeStrategy;
	private ProductTypeLazyTreeNodeLoader productTypeLazyTreeNodeLoader;

	private LazyTreeNode productTypeNode;
	private TreeNode selectedNode;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		initProductTypeNode();
	}

	public boolean canCreateProductType() {
		return selectedNode != null && ((AbstractProductUnit) selectedNode.getData()).isGroup();
	}

	public void onCreationDialogOpen() {
		RequestContext.getCurrentInstance().execute("PF('productTypeCategoriesPanelVar').hide()");
		RequestContext.getCurrentInstance().update("product_type_creation_form-product_type_creation_dlg");
		RequestContext.getCurrentInstance().execute("PF('productTypeCreationDlgVar').show()");
	}

	public void onProductTypeCreated() {
		AbstractProductUnit newAbstractProductUnit = productTypeCreationDialogModel.create();
		addNode(newAbstractProductUnit, !newAbstractProductUnit.isGroup() ? selectedNode : productTypeNode);
	}

	public void remove() {
		if (selectedNode.getChildCount() == 0) {
			AbstractProductUnit removableProductTypeUnit = (AbstractProductUnit) selectedNode.getData();
			TreeNode parent = selectedNode.getParent();
			parent.getChildren().remove(selectedNode);
			setSelectedNode(null);
			productTypeDirectoryViewState.setProductTypeUnit(null);
			em.remove(removableProductTypeUnit.getWrappedEntity());
			// Notification.info(messages.locationRemoved(),
			// messages.locationSuccessfullyRemoved(location.getObjectName()));
		} else {
			OverallMessagesBundle messages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			Notification.error(messages.cannotDeleteObject(), messages.objectHasDependentObjects());
		}
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void initProductTypeNode() {
		productTypeLazyTreeNodeLoader = new ProductTypeLazyTreeNodeLoader(productTypeRepository);
		productTypeLazyTreeNodeStrategy = new ProductTypeLazyTreeNodeStrategy();
		AbstractProductUnit currentProductTypeUnit = productTypeDirectoryViewState.getProductTypeUnit();
		if (currentProductTypeUnit != null) {
			productTypeLazyTreeNodeStrategy.setExpandedUnits(currentProductTypeUnit.getParents());
			productTypeLazyTreeNodeStrategy.setSelectedUnit(currentProductTypeUnit);
		}
		productTypeNode = LazyTreeNode.createRoot(loadRootNodes(), productTypeLazyTreeNodeStrategy,
				productTypeLazyTreeNodeLoader);
		selectedNode = productTypeLazyTreeNodeStrategy.getSelectedNode();
	}

	private List<AbstractProductUnit> loadRootNodes() {
		List<AbstractProductUnit> productUnits = ProductTypeGroupUnit
				.wrap(productTypeRepository.getAllProductTypeGroups());
		Collections.sort(productUnits, productTypeLazyTreeNodeStrategy.getSortingComparator());
		return productUnits;
	}

	private void addNode(AbstractProductUnit newAbstractProductUnit, TreeNode parentNode) {
		TreeNode newProductTypeNode = LazyTreeNode.create(Lists.newArrayList(newAbstractProductUnit),
				productTypeLazyTreeNodeStrategy, productTypeLazyTreeNodeLoader, parentNode).get(0);
		parentNode.getChildren().add(newProductTypeNode);
		((LazyTreeNode) parentNode).setLeaf(false);
		selectNode(newProductTypeNode);
	}

	private void selectNode(TreeNode newSelectedNode) {
		if (selectedNode != null) {
			selectedNode.setSelected(false);
			selectedNode.setExpanded(true);
		}
		newSelectedNode.setSelected(true);
		setSelectedNode(newSelectedNode);
		productTypeLazyTreeNodeStrategy.setSelectedNode((LazyTreeNode) newSelectedNode);
		productTypeLazyTreeNodeStrategy
				.setExpandedUnits(Sets.newHashSet((AbstractProductUnit) newSelectedNode.getData()));
		selectedNode.getParent().getChildren().stream().filter(treeNode -> !treeNode.equals(selectedNode))
				.forEach(treeNode -> treeNode.setSelected(false));
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public LazyTreeNode getProductTypeNode() {
		return productTypeNode;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		productTypeDirectoryViewState
				.setProductTypeUnit(selectedNode != null ? (AbstractProductUnit) selectedNode.getData() : null);
		this.selectedNode = selectedNode;
	}

}