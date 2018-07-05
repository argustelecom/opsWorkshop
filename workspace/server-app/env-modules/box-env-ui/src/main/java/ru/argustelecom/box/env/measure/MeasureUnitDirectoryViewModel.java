package ru.argustelecom.box.env.measure;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.measure.model.BaseMeasureUnit;
import ru.argustelecom.box.env.measure.model.DerivedMeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.measure.nls.MeasureMessagesBundle;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class MeasureUnitDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = -3360367387369009305L;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private MeasureUnitRepository measureUnitRepository;

	private TreeNode treeModelRoot;
	private Map<Long, TreeNode> idToNode = new HashMap<>();
	private TreeNode selectedNode;
	private TreeNode selectedActiveNode;
	private TreeNode selectedInvalidNode;
	private TreeNode treeModelInvalid;

	private List<BaseMeasureUnit> baseMeasures;
	private List<DerivedMeasureUnit> invalidMeasures;
	private List<MeasureUnit> activeMeasures;
	private List<MeasureUnit> defaultMeasures;

	@PostConstruct
	@Override
	public void postConstruct() {
		super.postConstruct();
		buildDefaultMeasures();
		buildTree();
		unitOfWork.makePermaLong();
	}

	private void buildDefaultMeasures() {
		String query = "from BaseMeasureUnit m";
		defaultMeasures = em.createQuery(query, MeasureUnit.class).getResultList();
	}

	public void changeMeasureGroupTreeModel() {
		DerivedMeasureUnit rm = (DerivedMeasureUnit) selectedNode.getData();
		if (rm.isInvalid()) {
			return;
		}
		removeSelectedMeasureFromOldGroup();
		addSelectedMeasureToNewGroup();
		expandTreeToSelectedNode();
	}

	private void removeSelectedMeasureFromOldGroup() {
		selectedNode.getParent().getChildren().remove(selectedNode);
		selectedInvalidNode = null;
	}

	private void addSelectedMeasureToNewGroup() {
		long groupId = ((MeasureUnit) selectedNode.getData()).getGroup().getId();
		TreeNode newParentNode = idToNode.get(groupId);
		List<TreeNode> children = newParentNode.getChildren();
		children.add(selectedNode);
		selectedActiveNode = selectedNode;
		Collections.sort(children, new NodeComparator());
		selectedNode.setParent(newParentNode);
	}

	private void expandTreeToSelectedNode() {
		TreeNode parentNode = selectedNode.getParent();
		while (parentNode != null) {
			parentNode.setExpanded(true);
			parentNode = parentNode.getParent();
		}
	}

	private void buildTree() {
		buildGroupNodes();
		addResourcesMeasure();
		buildInvalidMeasures();
	}

	private void buildGroupNodes() {
		idToNode = new HashMap<>();
		treeModelRoot = new DefaultTreeNode("root");
		List<BaseMeasureUnit> groups = getGroups();
		for (BaseMeasureUnit group : groups) {
			TreeNode node = new DefaultTreeNode("Group", group, treeModelRoot);
			idToNode.put(group.getId(), node);
		}

		// TreeNode node = new DefaultTreeNode("NoGroup", "Без группы", treeModelRoot);
		// idToNode.put(INVALID_MEASURE_ID, node);
	}

	public List<BaseMeasureUnit> getGroups() {
		if (baseMeasures == null) {
			baseMeasures = em.createQuery("from BaseMeasureUnit b", BaseMeasureUnit.class).getResultList();
		}
		return baseMeasures;
	}

	private void addResourcesMeasure() {
		List<MeasureUnit> measures = getActiveMeasures();
		for (MeasureUnit measure : measures) {
			TreeNode group = getGroupNode(measure);
			new DefaultTreeNode("MeasureUnit", measure, group);
		}
	}

	private void buildInvalidMeasures() {
		treeModelInvalid = new DefaultTreeNode("root");
		List<DerivedMeasureUnit> measures = getInvalidMeasures();
		for (MeasureUnit measure : measures) {
			new DefaultTreeNode("MeasureUnit", measure, treeModelInvalid);
		}
	}

	private List<MeasureUnit> getActiveMeasures() {
		if (activeMeasures == null) {
			String query = "from MeasureUnit m where m.class = 'BaseMeasureUnit' or (m.factor != null and m.group != null)";
			activeMeasures = em.createQuery(query, MeasureUnit.class).getResultList();
			Collections.sort(activeMeasures, new MeasureComparator());
		}
		return activeMeasures;
	}

	private List<DerivedMeasureUnit> getInvalidMeasures() {
		if (invalidMeasures == null) {
			String query = "from DerivedMeasureUnit m where m.factor = null or m.group = null";
			invalidMeasures = em.createQuery(query, DerivedMeasureUnit.class).getResultList();
			Collections.sort(invalidMeasures, new MeasureComparator());
		}
		return invalidMeasures;
	}

	public boolean isEditAttributesDisabled() {
		return isSelectedNodeBaseMeasureUnit() || isSelectSystemMeasure();

	}

	public void deleteMeasure() {
		MeasureUnit measure = (MeasureUnit) selectedNode.getData();
		OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		MeasureMessagesBundle measureMessages = LocaleUtils.getMessages(MeasureMessagesBundle.class);

		if (!isMeasureDeletable()) {
			em.remove(measure);
			deleteSelectedNodeFromModel();
			Notification.info(overallMessages.success(), measureMessages.measureDeleted(measure.getName()));
			selectedNode = null;
		} else {
			Notification.error(overallMessages.error(), measureMessages.measureCannotBeDeleted());
		}
	}

	private void deleteSelectedNodeFromModel() {
		selectedNode.getParent().getChildren().remove(selectedNode);
	}

	public boolean isMeasureDeletable() {
		return selectedNode == null || selectedNode.getType().equals("NoGroup")
				|| selectedNode.getType().equals("Group") || isSelectSystemMeasure() || isSelectedMeasureDefault();
	}

	public boolean isMeasureSelected() {
		return selectedNode != null && (selectedNode.getType().equals("MeasureUnit"));
	}

	private boolean isSelectedNodeBaseMeasureUnit() {
		if (selectedNode == null) {
			return false;
		}
		MeasureUnit measure = (MeasureUnit) selectedNode.getData();
		return measure instanceof BaseMeasureUnit;
	}

	private boolean isSelectSystemMeasure() {
		if (selectedNode == null) {
			return false;
		}
		MeasureUnit measure = (MeasureUnit) selectedNode.getData();
		return measure.getIsSys();
	}

	private TreeNode getGroupNode(MeasureUnit measure) {
		if (measure.getGroup() == null) {
			return null;
			// return idToNode.get(INVALID_MEASURE_ID);
		}
		long groupId = measure.getGroup().getId();
		return idToNode.get(groupId);
	}

	public TreeNode getTreeModelRoot() {
		return treeModelRoot;
	}

	public TreeNode getTreeModelInvalid() {
		return treeModelInvalid;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public void onTabChange(TabChangeEvent event) {
		MeasureMessagesBundle messages = LocaleUtils.getMessages(MeasureMessagesBundle.class);
		String title = event.getTab().getTitle();

		if (title.equals(messages.active())) {
			selectedInvalidNode = selectedNode;
			selectedNode = selectedActiveNode;
			return;
		}

		if (title.equals(messages.notSpecified())) {
			selectedActiveNode = selectedNode;
			selectedNode = selectedInvalidNode;
		}
	}

	public Long getSelectedMeasureFactor() {
		if (selectedNode == null) {
			return null;
		}
		MeasureUnit measure = (MeasureUnit) selectedNode.getData();
		if (measure instanceof DerivedMeasureUnit) {
			DerivedMeasureUnit dm = (DerivedMeasureUnit) measure;
			return dm.getFactor();
		}
		return 1L;
	}

	public void setSelectedMeasureFactor(Long factor) {
		MeasureUnit measure = (MeasureUnit) selectedNode.getData();
		if (measure instanceof DerivedMeasureUnit) {
			DerivedMeasureUnit dm = (DerivedMeasureUnit) measure;
			dm.setFactor(factor);
		}
	}

	public boolean isSelectedMeasureBasic() {
		if (!isMeasureSelected()) {
			return false;
		}
		MeasureUnit rm = (MeasureUnit) selectedNode.getData();
		return rm instanceof BaseMeasureUnit;
	}

	public boolean isSelectedMeasureDefault() {
		if (!isMeasureSelected()) {
			return false;
		}
		MeasureUnit rm = (MeasureUnit) selectedNode.getData();
		return defaultMeasures.contains(rm);
	}

	public class MeasureComparator implements Comparator<MeasureUnit> {
		MeasureUnit o1;
		MeasureUnit o2;

		@Override
		public int compare(MeasureUnit o1, MeasureUnit o2) {
			this.o1 = o1;
			this.o2 = o2;

			if (o1.equals(o2)) {
				return 0;
			}

			if (isSameGroup()) {
				return compareMeasuresOfGroup();
			}

			return compareGroup();
		}

		private boolean isSameGroup() {
			return Objects.equals(o1.getGroup(), o2.getGroup());
		}

		private int compareMeasuresOfGroup() {
			if (o1 instanceof BaseMeasureUnit) {
				return -1;
			} else if (o2 instanceof BaseMeasureUnit) {
				return 1;
			} else {
				return compareFactors();
			}
		}

		private int compareFactors() {
			Long factor1 = ((DerivedMeasureUnit) o1).getFactor();
			Long factor2 = ((DerivedMeasureUnit) o2).getFactor();
			if (factor1 == null && factor2 == null) {
				return 0;
			}
			if (factor1 == null) {
				return 1;
			}
			if (factor2 == null) {
				return -1;
			}
			return Long.compare(factor1, factor2);
		}

		private int compareGroup() {
			BaseMeasureUnit group1 = o1.getGroup();
			BaseMeasureUnit group2 = o2.getGroup();
			if (group1 == null && group2 == null) {
				return 0;
			}
			if (group1 == null) {
				return 1;
			}
			if (group2 == null) {
				return -1;
			}
			return group1.getCode().compareTo(group2.getCode());
		}
	}

	public class NodeComparator implements Comparator<TreeNode> {
		MeasureComparator measureComparator = new MeasureComparator();

		@Override
		public int compare(TreeNode o1, TreeNode o2) {
			MeasureUnit rm1 = (MeasureUnit) o1.getData();
			MeasureUnit rm2 = (MeasureUnit) o2.getData();
			return measureComparator.compare(rm1, rm2);
		}
	}

	public String getMessage(MeasureUnit measure) {
		String message = "";
		MeasureMessagesBundle measureMessages = LocaleUtils.getMessages(MeasureMessagesBundle.class);

		if (measure.getGroup() == null) {
			message += measureMessages.groupNotSpecified() + " ";
		}
		if (measure instanceof DerivedMeasureUnit && ((DerivedMeasureUnit) measure).getFactor() == null) {
			message += measureMessages.coefficientNotSpecified() + " ";
		}

		return message;
	}

	public Callback<DerivedMeasureUnit> getCallback() {
		return (measure -> {
			TreeNode group = getGroupNode(measure);
			if (selectedNode != null) {
				selectedNode.setSelected(false);
			}
			DefaultTreeNode node = new DefaultTreeNode("MeasureUnit", measure, group);
			node.setSelected(true);
			selectedNode = node;
			selectedActiveNode = selectedNode;
			expandTreeToSelectedNode();
		});
	}

}