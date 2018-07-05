package ru.argustelecom.box.env.document.type;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.document.type.DocumentTypeCategory.BILL_TYPE;
import static ru.argustelecom.box.env.document.type.DocumentTypeCategory.CONTRACT_EXTENSION_TYPE;
import static ru.argustelecom.box.env.document.type.DocumentTypeCategory.CONTRACT_TYPE;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.BillType.BillTypeQuery;
import ru.argustelecom.box.env.contract.ContractExtensionTypeDtoTranslator;
import ru.argustelecom.box.env.contract.ContractTypeDtoTranslator;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.contract.model.ContractExtensionType.ContractExtensionTypeQuery;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.contract.model.ContractType.ContractTypeQuery;
import ru.argustelecom.box.env.document.model.DocumentType;
import ru.argustelecom.box.env.document.type.tree.AbstractDocumentTypeUnit;
import ru.argustelecom.box.env.document.type.tree.DocumentTypeCategoryUnit;
import ru.argustelecom.box.env.document.type.tree.DocumentTypeUnit;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "documentTypeDirectoryVm")
@PresentationModel
public class DocumentTypeDirectoryViewModel extends ViewModel {

	public static final String CATEGORY_NODE_TYPE = "categoryNodeType";

	@Inject
	private DocumentTypeDirectoryViewState viewState;

	@Inject
	private ContractTypeDtoTranslator contractTypeDtoTranslator;

	@Inject
	private ContractExtensionTypeDtoTranslator contractExtensionTypeDtoTranslator;

	@Inject
	private BillTypeDtoTranslator billTypeDtoTranslator;

	private Map<Serializable, TreeNode> nodeIndex = new HashMap<>();
	private TreeNode documentTypeTree;
	private TreeNode selectedNode;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		refresh();
	}

	protected void refresh() {
		if (documentTypeTree == null) {
			documentTypeTree = createTree();
		}
		restoreViewState();
	}

	private TreeNode createTree() {
		nodeIndex.clear();
		TreeNode root = new DefaultTreeNode("root", null);
		createDocumentTypeTree(root, CONTRACT_TYPE, contractTypeDtoTranslator,
				new ContractTypeQuery<>(ContractType.class));
		createDocumentTypeTree(root, CONTRACT_EXTENSION_TYPE, contractExtensionTypeDtoTranslator,
				new ContractExtensionTypeQuery<>(ContractExtensionType.class));
		createDocumentTypeTree(root, BILL_TYPE, billTypeDtoTranslator, new BillTypeQuery<>(BillType.class));
		return root;
	}

	private <DTO extends DocumentTypeDto, I extends DocumentType, T extends DefaultDtoTranslator<DTO, I>> void createDocumentTypeTree(
			TreeNode root, DocumentTypeCategory category, T translator, EntityQuery<I> query) {
		final TreeNode categoryNode = createNode(root, CATEGORY_NODE_TYPE, new DocumentTypeCategoryUnit(category));
		query.createTypedQuery(em).getResultList().forEach(documentType -> createNode(categoryNode,
				category.getKeyword(), new DocumentTypeUnit(translator.translate(documentType))));
	}

	private TreeNode createNode(TreeNode parent, String nodeType, AbstractDocumentTypeUnit<?> nodeData) {
		final TreeNode node = new DefaultTreeNode(nodeType, nodeData, parent);
		nodeIndex.put(nodeData.getId(), node);
		return node;
	}

	private void restoreViewState() {
		if (viewState.getDocumentTypeUnit() == null) {
			if (selectedNode != null) {
				selectedNode.setSelected(false);
			}
			selectedNode = null;
		} else {
			selectedNode = nodeIndex.get(viewState.getDocumentTypeUnit().getId());
			select(selectedNode);
		}
	}

	private void select(TreeNode node) {
		if (node != null) {
			node.setSelected(true);
			expandParentNodeRecursively(node);
		}
	}

	private void expandParentNodeRecursively(TreeNode node) {
		TreeNode parentNode = node.getParent();
		if (parentNode != null) {
			parentNode.setExpanded(true);
			expandParentNodeRecursively(parentNode);
		}
	}

	public TreeNode getDocumentTypeTree() {
		return documentTypeTree;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		if (selectedNode == null) {
			viewState.setDocumentTypeUnit(null);
		} else {
			AbstractDocumentTypeUnit<?> nodeData = (AbstractDocumentTypeUnit<?>) selectedNode.getData();
			viewState.setDocumentTypeUnit(nodeData);
		}
		this.selectedNode = selectedNode;
	}

	public DocumentTypeDto getCurrentDocumentTypeDto() {
		return viewState.getDocumentTypeUnit() != null ? viewState.getDocumentTypeUnit().getDocumentTypeDto() : null;
	}

	public DocumentTypeCategory getCurrentCategory() {
		return viewState.getDocumentTypeUnit() != null ? viewState.getDocumentTypeUnit().getCategory() : null;
	}

	public boolean canRemoveCurrentDocumentType() {
		return getCurrentDocumentTypeDto() != null;
	}

	public void removeCurrentDocumentType() {
		checkState(viewState.getDocumentTypeUnit() != null);

		// В этот метод нельзя попасть, если текущий выделенный узел не DocumentType. Это обеспечивается на уровне
		// проверки доступности действия, поэтому отсутствие здесь DocumentType следует воспринимать как системное
		// исключение
		Identifiable removableDocumentType = checkNotNull(getCurrentDocumentTypeDto().getIdentifiable(em));
		TreeNode removableNode = nodeIndex.get(viewState.getDocumentTypeUnit().getId());
		TreeNode parentNode = removableNode.getParent();
		TreeNode nearestSibling = getNearestSibling(parentNode, removableNode);

		// Сбросить текущий CurrentType для избежания ошибки EntityNotFoundException
		viewState.resetCurrentType();
		if (parentNode.getChildren().remove(removableNode)) {
			// Явный flush, чтобы получить ошибку удаления как можно раньше, до конца реквеста
			em.remove(removableDocumentType);
			em.flush();
		}

		// Если flush выполнился успешно, то можно перейти на новый узел взамен удаленного. Если flush провалился из-за
		// наличия связанных объектов, то viewState не изменится и CurrentType будет проинициализирован повторно
		if (this.selectedNode != null) {
			this.selectedNode.setSelected(false);
		}
		setSelectedNode(nearestSibling);
		select(nearestSibling);
	}

	private TreeNode getNearestSibling(TreeNode parent, TreeNode node) {
		if (parent.getChildCount() == 1) {
			return parent;
		}
		int index = parent.getChildren().indexOf(node);
		checkState(index >= 0);
		return index == 0 ? parent.getChildren().get(++index) : parent.getChildren().get(--index);
	}

	public Callback<DocumentTypeDto> getDocumentTypeCallback() {
		return (newDocTypeDto -> {
			checkState(newDocTypeDto != null, "Unable to create new document type");

			DocumentTypeCategory category = DocumentTypeCategory
					.getDocumentTypeCategory(newDocTypeDto.getEntityClass());

			TreeNode parentNode = nodeIndex.get(category);
			checkState(parentNode != null, "Unable to find category node");

			TreeNode newDocTypeNode = createNode(parentNode, category.getKeyword(),
					new DocumentTypeUnit(newDocTypeDto));

			if (this.selectedNode != null) {
				this.selectedNode.setSelected(false);
			}
			setSelectedNode(newDocTypeNode);
			select(newDocTypeNode);
		});
	}

	private static final long serialVersionUID = 6269058192908488679L;

}
