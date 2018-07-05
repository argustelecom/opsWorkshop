package ru.argustelecom.box.env.billing.provision;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.billing.provision.ProvisionTermsDirectoryViewModel.TermsNodeType.RECURRENT;
import static ru.argustelecom.box.env.billing.provision.ProvisionTermsDirectoryViewModel.TermsNodeType.ROOT;
import static ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState.ACTIVE;
import static ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState.ARCHIVE;
import static ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState.FORMALIZATION;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import lombok.Getter;
import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState;
import ru.argustelecom.box.env.billing.provision.nls.ProvisionTermsMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.event.RoutingCompleted;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.publang.billing.model.IRecurrentTerms;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "provisionTermsDirectoryVM")
@PresentationModel
public class ProvisionTermsDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = -6909662967542932080L;

	private static final Logger log = Logger.getLogger(ProvisionTermsDirectoryViewModel.class);

	@Inject
	private ProvisionTermsRepository ptr;

	@Inject
	private CurrentProvisionTerms currentTerms;

	@Getter
	private TreeNode termsNode;
	private TreeNode recurrentNode;

	@Getter
	private TreeNode selectedNode;

	private AbstractProvisionTerms terms;

	@PostConstruct
	@Override
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();

		refresh();
		initTree();
	}

	public Callback<RecurrentTerms> getCallback() {
		return terms -> {
			this.terms = terms;
			currentTerms.setValue(terms);
			markSelected(new DefaultTreeNode(terms, findOrCreateStateNode(FORMALIZATION)));
		};
	}

	public void remove() {
		currentTerms.setValue(null);
		terms = null;

		ptr.removeRecurrentTerms((RecurrentTerms) selectedNode.getData());
		removeNode(selectedNode);

		selectedNode = null;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		if (selectedNode != null && selectedNode.getData() instanceof AbstractProvisionTerms) {
			terms = (AbstractProvisionTerms) selectedNode.getData();
			currentTerms.setValue(terms);
			this.selectedNode = selectedNode;
		} else {
			this.selectedNode = null;
			this.terms = null;
			currentTerms.setValue(null);
		}
	}

	public AbstractProvisionTerms getTerms() {
		return initializeAndUnproxy(terms);
	}

	public boolean canRemoveTerms() {
		return terms != null && terms instanceof RecurrentTerms
				&& ((RecurrentTerms) terms).getState().equals(FORMALIZATION);
	}

	//@formatter:off
	void afterActivation(@Observes(notifyObserver = Reception.IF_EXISTS)
							 @RoutingCompleted(
							 		oldState = IRecurrentTerms.State.FORMALIZATION,
									newState = IRecurrentTerms.State.ACTIVE)
							 IRecurrentTerms iRecurrentTerms) {
	//@formatter:on
		moveToOtherParent(iRecurrentTerms, FORMALIZATION, ACTIVE);
	}

	//@formatter:off
	public void afterArchivation(@Observes(notifyObserver = Reception.IF_EXISTS)
								 @RoutingCompleted(
										oldState = IRecurrentTerms.State.ACTIVE,
										newState = IRecurrentTerms.State.ARCHIVE)
								 IRecurrentTerms iRecurrentTerms) {
	//@formatter:on
		moveToOtherParent(iRecurrentTerms, ACTIVE, ARCHIVE);
	}

	private void moveToOtherParent(IRecurrentTerms iRecurrentTerms, RecurrentTermsState oldState,
			RecurrentTermsState newState) {
		Optional<TreeNode> changedNodeOptional = findOrCreateStateNode(oldState).getChildren().stream()
				.filter(rt -> ((RecurrentTerms) rt.getData()).getId().equals(iRecurrentTerms.getId())).findFirst();
		if (changedNodeOptional.isPresent()) {
			TreeNode changedNode = changedNodeOptional.get();
			removeNode(changedNode);
			findOrCreateStateNode(newState).getChildren().add(changedNode);
			markSelected(changedNode);
		}
	}

	private void initTree() {
		termsNode = new DefaultTreeNode(ROOT.getType(), null);
		recurrentNode = new DefaultTreeNode(RECURRENT.getType(), RECURRENT.getName(), termsNode);

		List<AbstractProvisionTerms> allProvisionTerms = ptr.getAllProvisionTerms();
		Map<RecurrentTermsState, List<AbstractProvisionTerms>> recurrentTermsStateMap = allProvisionTerms.stream()
				.filter(AbstractProvisionTerms::isRecurrent)
				.collect(groupingBy(t -> ((RecurrentTerms) t).getState(), mapping(identity(), toList())));

		// сделано не через Map.forEach для сортировки
		Arrays.stream(RecurrentTermsState.values()).forEach(s -> fillStateNode(s, recurrentTermsStateMap.get(s)));
		allProvisionTerms.stream().filter(t -> !t.isRecurrent()).forEach(nonRt -> addNode(nonRt, termsNode));
	}

	private void removeNode(TreeNode removableNode) {
		TreeNode parentForRemovable = removableNode.getParent();
		parentForRemovable.getChildren().remove(removableNode);
		if (parentForRemovable.getChildren().isEmpty())
			parentForRemovable.getParent().getChildren().remove(parentForRemovable);
	}

	private void fillStateNode(RecurrentTermsState state, List<AbstractProvisionTerms> termsList) {
		if (termsList == null || termsList.isEmpty())
			return;

		TreeNode stateNode = createStateNode(state);
		termsList.forEach(terms -> addNode(terms, stateNode));
	}

	private void addNode(AbstractProvisionTerms value, TreeNode parentNode) {
		TreeNode node = new DefaultTreeNode(value, parentNode);
		if (terms != null && terms.equals(value)) {
			markSelected(node);
		}
	}

	private DefaultTreeNode createStateNode(RecurrentTermsState state) {
		return new DefaultTreeNode(state.toString().toLowerCase(), state.getName(), recurrentNode);
	}

	private TreeNode findOrCreateStateNode(RecurrentTermsState state) {
		Optional<TreeNode> stateNodeOptional = recurrentNode.getChildren().stream()
				.filter(sn -> sn.getType().toUpperCase().equals(state.toString())).findFirst();
		return stateNodeOptional.orElseGet(() -> createStateNode(state));
	}

	private void markSelected(TreeNode node) {
		if (selectedNode != null)
			selectedNode.setSelected(false);

		selectedNode = node;
		node.setSelected(true);
		expandParents(selectedNode.getParent());
	}

	private void expandParents(TreeNode node) {
		node.setExpanded(true);
		if (node.getParent() != null)
			expandParents(node.getParent());
	}

	private void refresh() {
		if (currentTerms.changed(terms)) {
			terms = currentTerms.getValue();
			log.debugv("postConstruct. abstract_provision_terms_id={0}", terms.getId());
		}
	}

	public enum TermsNodeType {

		//@formatter:off
		ROOT 		("root"),
		RECURRENT	("recurrent");
		//@formatter:on

		@Getter
		private String type;

		TermsNodeType(String type) {
			this.type = type;
		}

		public String getName() {
			switch (this) {
			case ROOT:
				return "";
			case RECURRENT:
				return LocaleUtils.getMessages(ProvisionTermsMessagesBundle.class).recurrent();
			default:
				throw new SystemException("Unsupported TermsNodeType");
			}

		}

	}

}