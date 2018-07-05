package ru.argustelecom.box.env.report;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import ru.argustelecom.system.inf.tree.LazyTreeNode;
import ru.argustelecom.system.inf.tree.LazyTreeNodeStrategy;

public class ReportTypeLazyTreeNodeStrategy implements LazyTreeNodeStrategy<ReportTypeTreeNodeDto> {

	public static final Comparator<ReportTypeTreeNodeDto> NAME_COMPARATOR = Comparator
			.comparing(ReportTypeTreeNodeDto::getName);

	private Set<ReportTypeTreeNodeDto> expandedUnits = new HashSet<>();
	private ReportTypeTreeNodeDto selectedUnit;
	private LazyTreeNode<ReportTypeTreeNodeDto> selectedNode;

	@Override
	public void setExpandedUnits(Set<ReportTypeTreeNodeDto> expandedUnits) {
		this.expandedUnits = expandedUnits;
	}

	@Override
	public void setSelectedUnits(Set<ReportTypeTreeNodeDto> selectedUnits) {
	}

	@Override
	public void apply(LazyTreeNode<ReportTypeTreeNodeDto> treeNode) {
		ReportTypeTreeNodeDto node = (ReportTypeTreeNodeDto) treeNode.getData();
		ReportTypeCategory nodeType = node.getCategory();

		treeNode.setExpanded(expandedUnits.contains(node));
		if (node.equals(selectedUnit)) {
			treeNode.setSelected(true);
			selectedNode = treeNode;
		}
		treeNode.setSelectable(true);
		treeNode.setType(nodeType.name());
		treeNode.setLeaf(treeNode.getChildren().isEmpty() || node.getCategory() == ReportTypeCategory.TYPE);
	}

	@Override
	public void onNodeSelect(LazyTreeNode<ReportTypeTreeNodeDto> node, boolean selected) {
	}

	@Override
	public Comparator<ReportTypeTreeNodeDto> getSortingComparator() {
		return NAME_COMPARATOR;
	}

	public void setSelectedUnit(ReportTypeTreeNodeDto selectedUnit) {
		this.selectedUnit = selectedUnit;
	}

	public LazyTreeNode<ReportTypeTreeNodeDto> getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(LazyTreeNode<ReportTypeTreeNodeDto> newSelectedNode) {
		this.selectedNode = newSelectedNode;
		if (selectedNode != null) {
			selectedNode.setSelected(true);
			selectedNode.setLeaf(selectedNode.getChildren().isEmpty());
			if (selectedNode.getParent() != null) {
				selectedNode.getParent().setExpanded(true);
			}
		}
	}
}
