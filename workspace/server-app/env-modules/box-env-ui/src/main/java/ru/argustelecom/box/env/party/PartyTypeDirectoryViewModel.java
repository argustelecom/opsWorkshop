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

import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "partyTypeDirectoryVm")
@PresentationModel
public class PartyTypeDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = -4221557663822235310L;

	private static final Logger log = Logger.getLogger(PartyTypeDirectoryViewModel.class);

	@Inject
	private PartyTypeCreationDialogModel partyTypeCreationDialogModel;

	@Inject
	private PartyTypeRepository partyTypeRepository;

	private PartyType partyType;

	private TreeNode partyTypeNode;
	private TreeNode personNode;
	private TreeNode companyNode;
	private TreeNode selectedNode;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		loadPartyTypeNode();
	}

	public void onPartyTypeCreated() {
		cleanCurrentPartyType();
		PartyType newPartyType = partyTypeCreationDialogModel.create();
		addPartyTypeNode(newPartyType, true);
	}

	public void removePartyType() {
		cleanCurrentPartyType();
		if (selectedNode != null) {
			em.remove(selectedNode.getData());
			selectedNode.getParent().getChildren().remove(selectedNode);
			setSelectedNode(null);
		}
	}

	public boolean isRemovableNode() {
		return selectedNode != null && !selectedNode.equals(personNode) && !selectedNode.equals(companyNode);
	}

	public PartyCategory[] getPartyCategories() {
		return PartyCategory.values();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void loadPartyTypeNode() {
		partyTypeNode = new DefaultTreeNode(ROOT, null);
		personNode = new DefaultTreeNode(PERSON.getKeyword(), PERSON, partyTypeNode);
		companyNode = new DefaultTreeNode(COMPANY.getKeyword(), COMPANY, partyTypeNode);

		partyTypeRepository.getAllPartyTypes().forEach(type -> addPartyTypeNode(type, false));
		sort(personNode.getChildren());
		sort(companyNode.getChildren());
	}

	private TreeNode addPartyTypeNode(PartyType type, boolean markSelected) {
		TreeNode treeNode = new DefaultTreeNode(PARTY_TYPE.getKeyword(), type,
				type.getCategory().equals(PartyCategory.PERSON) ? personNode : companyNode);
		if (markSelected || type.equals(partyType)) {
			if (selectedNode != null)
				selectedNode.setSelected(false);
			treeNode.getParent().setExpanded(true);
			treeNode.setSelected(true);
			setSelectedNode(treeNode);
		}
		return treeNode;
	}

	private void cleanCurrentPartyType() {
		partyType = null;
	}

	public void sort(List<TreeNode> nodes) {
		nodes.sort(DEFAULT_SORTING_COMPARATOR);
	}

	private static final Comparator<TreeNode> DEFAULT_SORTING_COMPARATOR = (o1, o2) -> ((PartyType) o1.getData())
			.getName().compareTo(((PartyType) o2.getData()).getName());

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public PartyType getPartyType() {
		return partyType;
	}

	public TreeNode getPartyTypeNode() {
		return partyTypeNode;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		if (selectedNode != null && selectedNode.getData() instanceof PartyType) {
			partyType = (PartyType) selectedNode.getData();
		} else {
			cleanCurrentPartyType();
		}
		this.selectedNode = selectedNode;
	}

}