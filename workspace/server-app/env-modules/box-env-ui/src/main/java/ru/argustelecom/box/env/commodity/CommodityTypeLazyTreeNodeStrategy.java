package ru.argustelecom.box.env.commodity;

import static ru.argustelecom.box.env.commodity.CommodityTypeRef.GROUP;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.system.inf.tree.LazyTreeNode;
import ru.argustelecom.system.inf.tree.LazyTreeNodeStrategy;

public class CommodityTypeLazyTreeNodeStrategy implements LazyTreeNodeStrategy<CommodityTypeTreeNodeDto> {

	private Set<CommodityTypeTreeNodeDto> expandedUnits = new HashSet<>();

	@Setter
	private CommodityTypeTreeNodeDto selectedUnit;

	@Getter
	@Setter
	private LazyTreeNode selectedNode;

	@Override
	public void setExpandedUnits(Set<CommodityTypeTreeNodeDto> expandedUnits) {
		this.expandedUnits = expandedUnits;
	}

	@Override
	public void setSelectedUnits(Set<CommodityTypeTreeNodeDto> selectedUnits) {
	}

	@Override
	public void apply(LazyTreeNode<CommodityTypeTreeNodeDto> node) {
		CommodityTypeTreeNodeDto nodeData = (CommodityTypeTreeNodeDto) node.getData();
		CommodityTypeRef nodeType = nodeData.getType();

		node.setExpanded(expandedUnits.contains(nodeData));
		if (nodeData.equals(selectedUnit)) {
			node.setSelected(true);
			selectedNode = node;
		}
		node.setSelectable(true);
		node.setType(nodeType.name());
		node.setLeaf(isLeaf(node));
	}

	private boolean isLeaf(LazyTreeNode<CommodityTypeTreeNodeDto> node) {
		return !((CommodityTypeTreeNodeDto) node.getData()).isGroup() || node.getChildren().isEmpty();
	}

	@Override
	public void onNodeSelect(LazyTreeNode<CommodityTypeTreeNodeDto> node, boolean selected) {
	}

	@Override
	public Comparator<CommodityTypeTreeNodeDto> getSortingComparator() {
		return NAME_COMPARATOR;
	}

	private static final Comparator<CommodityTypeTreeNodeDto> NAME_COMPARATOR = (o1, o2) -> {
		if (o1.getType().equals(GROUP) && o2.getType().equals(GROUP)) {
			return o1.getName().compareTo(o2.getName());
		} else if (o1.getType().equals(GROUP)) {
			return -1;
		} else if (o2.getType().equals(GROUP)) {
			return 1;
		} else if (!o1.getType().equals(o2.getType())) {
			return o1.getType().compareTo(o2.getType());
		}
		return o1.getName().compareTo(o2.getName());
	};

}