package ru.argustelecom.box.env.product;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.product.model.ProductTypeGroup;
import ru.argustelecom.system.inf.tree.LazyTreeNode;
import ru.argustelecom.system.inf.tree.LazyTreeNodeStrategy;

public class ProductTypeLazyTreeNodeStrategy implements LazyTreeNodeStrategy<AbstractProductUnit> {

	private Set<AbstractProductUnit> expandedUnits = new HashSet<>();
	private AbstractProductUnit selectedUnit;
	private LazyTreeNode selectedNode;

	@Override
	public void setExpandedUnits(Set<AbstractProductUnit> expandedUnits) {
		this.expandedUnits = expandedUnits;
	}

	@Override
	public void setSelectedUnits(Set<AbstractProductUnit> set) {
	}

	@Override
	public void apply(LazyTreeNode<AbstractProductUnit> lazyTreeNode) {
		AbstractProductUnit productTypeUnit = (AbstractProductUnit) lazyTreeNode.getData();

		lazyTreeNode.setExpanded(expandedUnits.contains(productTypeUnit));

		if (productTypeUnit.equals(selectedUnit)) {
			lazyTreeNode.setSelected(true);
			selectedNode = lazyTreeNode;
		}

		markLeaf(lazyTreeNode);

		lazyTreeNode.setSelectable(true);
		lazyTreeNode.setType(productTypeUnit.getNodeType());
	}

	@Override
	public void onNodeSelect(LazyTreeNode<AbstractProductUnit> lazyTreeNode, boolean b) {

	}

	@Override
	public Comparator<AbstractProductUnit> getSortingComparator() {
		return DEFAULT_SORTING_COMPARATOR;
	}

	public static final Comparator<AbstractProductUnit> DEFAULT_SORTING_COMPARATOR = (o1, o2) -> {
		if (o1 instanceof ProductTypeGroupUnit && o2 instanceof ProductTypeGroupUnit) {
			ProductTypeGroup pg1 = ((ProductTypeGroupUnit) o1).getProductTypeGroup();
			ProductTypeGroup pg2 = ((ProductTypeGroupUnit) o2).getProductTypeGroup();

			if (!pg1.getTypes().isEmpty() && pg2.getTypes().isEmpty()) {
				return -1;
			}
			if (pg1.getTypes().isEmpty() && !pg2.getTypes().isEmpty()) {
				return 1;
			}
		}
		if (o1 instanceof ProductTypeUnit && o2 instanceof ProductTypeCompositeUnit) {
			return 1;
		}
		if (o1 instanceof ProductTypeCompositeUnit && o2 instanceof ProductTypeUnit) {
			return -1;
		}

		String name1 = Strings.nullToEmpty(o1.getName());
		String name2 = Strings.nullToEmpty(o2.getName());
		return name1.compareTo(name2);
	};

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void markLeaf(LazyTreeNode node) {
		if (node.getData() instanceof ProductTypeUnit || node.getData() instanceof ProductTypeCompositeUnit
				|| node.getChildren().isEmpty())
			node.setLeaf(true);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public LazyTreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(LazyTreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public void setSelectedUnit(AbstractProductUnit selectedUnit) {
		this.selectedUnit = selectedUnit;
	}

}
