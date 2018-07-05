package ru.argustelecom.box.env.report;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.system.inf.tree.LazyTreeNode;
import ru.argustelecom.system.inf.tree.LazyTreeNodeStrategy;

public class ReportBandsLazyTreeNodeStrategy implements LazyTreeNodeStrategy<ReportBandDto> {

	@Getter
	@Setter
	private LazyTreeNode<ReportBandDto> selectedNode;

	private Set<ReportBandDto> expandedUnits = new HashSet<>();

	void addExpandedUnit(ReportBandDto unit) {
		expandedUnits.add(unit);
	}

	void removeExpandedUnit(ReportBandDto unit) {
		expandedUnits.remove(unit);
	}

	@Override
	public void setExpandedUnits(Set<ReportBandDto> expandedUnits) {
		this.expandedUnits = expandedUnits;
	}

	@Override
	public void setSelectedUnits(Set<ReportBandDto> selectedUnits) {
	}

	@Override
	public void apply(LazyTreeNode<ReportBandDto> treeNode) {
		ReportBandDto band = (ReportBandDto) treeNode.getData();

		if (expandedUnits.contains(band)) {
			treeNode.setExpanded(true);
			treeNode.setLeaf(false);
		}

		if (selectedNode != null && band.equals(selectedNode.getData())) {
			treeNode.setSelected(true);
		}

		if (treeNode.getChildren().isEmpty()) {
			treeNode.setLeaf(true);
		}
		treeNode.setSelectable(true);
		treeNode.setType("band");

	}

	@Override
	public void onNodeSelect(LazyTreeNode<ReportBandDto> treeNode, boolean selected) {
	}

	@Override
	public Comparator<ReportBandDto> getSortingComparator() {
		return null;
	}

}