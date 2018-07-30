package ru.argustelecom.box.env.party;

import static ru.argustelecom.box.env.party.PartyTypeNodeRef.COMPANY;
import static ru.argustelecom.box.env.party.PartyTypeNodeRef.PARTY_TYPE;
import static ru.argustelecom.box.env.party.PartyTypeNodeRef.PERSON;
import static ru.argustelecom.box.env.party.PartyTypeNodeRef.ROOT;

import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "customerTypeDirectoryVm")
@PresentationModel
public class CustomerTypeDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = -4221557663822235310L;

	private static final Logger log = Logger.getLogger(CustomerTypeDirectoryViewModel.class);

	@Inject
	private CustomerTypeCreationDialogModel customerTypeCreationDialogModel;

	@Inject
	private CustomerTypeRepository customerTypeRepository;

	private CustomerType customerType;

	private TreeNode customerTypeNode;
	private TreeNode personNode;
	private TreeNode companyNode;
	private TreeNode selectedNode;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		loadCustomerTypeNode();
	}

	public void onCustomerTypeCreated() {
		CustomerType newCustomerType = customerTypeCreationDialogModel.create();
		addCustomerTypeNode(newCustomerType, true);
	}

	public void removeCustomerType() {
		cleanCurrentCustomerType();
		if (selectedNode != null) {
			em.remove(selectedNode.getData());
			selectedNode.getParent().getChildren().remove(selectedNode);
			setSelectedNode(null);
		}
	}

	public boolean isRemovableNode() {
		return selectedNode != null && !selectedNode.equals(personNode) && !selectedNode.equals(companyNode);
	}

	public CustomerCategory[] getCustomerCategories() {
		return CustomerCategory.values();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void loadCustomerTypeNode() {
		customerTypeNode = new DefaultTreeNode(ROOT, null);
		personNode = new DefaultTreeNode(PERSON.getKeyword(), PERSON, customerTypeNode);
		companyNode = new DefaultTreeNode(COMPANY.getKeyword(), COMPANY, customerTypeNode);

		customerTypeRepository.getAllCustomerTypes()
				.forEach(type -> addCustomerTypeNode(type, type.equals(customerType)));
		sort(personNode.getChildren());
		sort(companyNode.getChildren());
	}

	private TreeNode addCustomerTypeNode(CustomerType type, boolean markSelected) {
		TreeNode treeNode = new DefaultTreeNode(PARTY_TYPE.getKeyword(), type,
				type.getCategory().equals(CustomerCategory.PERSON) ? personNode : companyNode);
		if (markSelected) {
			if (selectedNode != null)
				selectedNode.setSelected(false);
			treeNode.getParent().setExpanded(true);
			treeNode.setSelected(true);
			setSelectedNode(treeNode);
		}
		return treeNode;
	}

	private void cleanCurrentCustomerType() {
		customerType = null;
	}

	public void sort(List<TreeNode> nodes) {
		nodes.sort(DEFAULT_SORTING_COMPARATOR);
	}

	private static final Comparator<TreeNode> DEFAULT_SORTING_COMPARATOR = (o1, o2) -> ((CustomerType) o1.getData())
			.getName().compareTo(((CustomerType) o2.getData()).getName());

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public CustomerType getCustomerType() {
		return customerType;
	}

	public TreeNode getCustomerTypeNode() {
		return customerTypeNode;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		if (selectedNode != null && selectedNode.getData() instanceof CustomerType) {
			customerType = (CustomerType) selectedNode.getData();
		} else {
			cleanCurrentCustomerType();
		}
		this.selectedNode = selectedNode;
	}

}