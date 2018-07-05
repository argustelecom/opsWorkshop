package ru.argustelecom.box.env.companyinfo;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import lombok.Getter;
import ru.argustelecom.box.env.party.CurrentPartyRole;
import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

/**
 * <b>Presentation model</b> для справочника {@linkplain ru.argustelecom.box.env.party.model.role.Owner юридических лиц
 * компании}.
 *
 * <p>
 * <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6294086">Описание в Confluence</a>
 * </p>
 */
@Named(value = "companyInfoVm")
@PresentationModel
public class CompanyInfoViewModel extends ViewModel {

	private static final String OWNER_NODE_TYPE = "owner";

	@Inject
	private OwnerAppService ownerAs;

	@Inject
	private CompanyInfoOwnerDtoTranslator ownerDtoTr;

	@Inject
	private CurrentPartyRole currentOwner;

	@Inject
	private CompanyInfoViewState viewState;

	@Getter
	private TreeNode root;

	@Getter
	private TreeNode selectedNode;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		initCurrentOwner(ofNullable(currentOwner.getValue()).map(o -> ownerDtoTr.translate((Owner) o)).orElse(null));
		init();
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
		initCurrentOwner((CompanyInfoOwnerDto) ofNullable(selectedNode).map(TreeNode::getData).orElse(null));
		markSelected(selectedNode);
	}

	public Callback<CompanyInfoOwnerDto> getCallbackAfterCreation() {
		return owner -> {
			TreeNode node = new DefaultTreeNode(OWNER_NODE_TYPE, owner, root);
			selectedNode.setSelected(false);
			setSelectedNode(node);
			updatePrincipal(owner);
		};
	}

	public Callback<CompanyInfoOwnerDto> getCallbackAfterChangePrincipal() {
		return this::updatePrincipal;
	}

	public void removeSelectedOwner() {
		if (selectedNode != null) {
			Long removableOwnerId = viewState.getOwnerDto().getId();
			root.getChildren().remove(selectedNode);
			setSelectedNode(null);
			ownerAs.remove(removableOwnerId);
		}
	}

	public boolean canRemoveSelectedOwner() {
		CompanyInfoOwnerDto ownerDto = viewState.getOwnerDto();
		return selectedNode != null && ownerDto != null && !ownerDto.isPrincipal();
	}

	public void updatePrincipal(CompanyInfoOwnerDto changedOwner) {
		for (TreeNode child : root.getChildren()) {
			CompanyInfoOwnerDto owner = (CompanyInfoOwnerDto) child.getData();

			if (changedOwner.isPrincipal()) {
				if (!Objects.equals(owner, changedOwner)) {
					if (owner.isPrincipal()) {
						owner.setPrincipal(false);
					}
				}
			}
		}
	}

	private void init() {
		root = new DefaultTreeNode("root", null);

		List<CompanyInfoOwnerDto> owners = ownerAs.findAll().stream().map(ownerDtoTr::translate).collect(toList());
		owners.sort(Comparator.comparing(CompanyInfoOwnerDto::getName));
		owners.forEach(owner -> {
			TreeNode node = new DefaultTreeNode(OWNER_NODE_TYPE, owner, root);
			if (owner.equals(viewState.getOwnerDto())) {
				markSelected(node);
			}
		});
	}

	private void initCurrentOwner(CompanyInfoOwnerDto owner) {
		CompanyInfoOwnerDto selectedOwner = owner == null ? ownerDtoTr.translate(ownerAs.findPrincipal()) : owner;
		currentOwner.setValue((PartyRole) selectedOwner.getIdentifiable());
		viewState.setOwnerDto(selectedOwner);
	}

	private void markSelected(TreeNode node) {
		if (node != null) {
			node.setSelected(true);
		}
	}

	private static final long serialVersionUID = -599799827620254068L;

}