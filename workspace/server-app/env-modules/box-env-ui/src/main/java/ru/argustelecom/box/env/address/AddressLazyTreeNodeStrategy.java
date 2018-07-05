package ru.argustelecom.box.env.address;

import static ru.argustelecom.box.env.address.LocationCategory.COUNTRY;
import static ru.argustelecom.box.env.address.LocationCategory.REGION;
import static ru.argustelecom.box.env.address.LocationCategory.STREET;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import ru.argustelecom.box.env.address.model.Country;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.box.env.address.model.Street;
import ru.argustelecom.system.inf.tree.LazyTreeNode;
import ru.argustelecom.system.inf.tree.LazyTreeNodeStrategy;

public class AddressLazyTreeNodeStrategy implements LazyTreeNodeStrategy<Location> {

	private Set<Location> expandedUnits = new HashSet<>();
	private Location selectedUnit;
	private LazyTreeNode selectedNode;

	@Override
	public void setExpandedUnits(Set<Location> expanded) {
		this.expandedUnits = expanded;
	}

	@Override
	public void setSelectedUnits(Set<Location> selected) {
	}

	@Override
	public void apply(LazyTreeNode<Location> lazyTreeNode) {
		Location location = (Location) lazyTreeNode.getData();
		String nodeType = determineNodeType(location);

		lazyTreeNode.setExpanded(expandedUnits.contains(location));
		if (location.equals(selectedUnit)) {
			lazyTreeNode.setSelected(true);
			selectedNode = lazyTreeNode;
		}
		lazyTreeNode.setSelectable(true);
		lazyTreeNode.setType(nodeType);

		if (nodeType.equals(STREET.getKeyword())) {
			lazyTreeNode.setLeaf(true);
		}
	}

	@Override
	public void onNodeSelect(LazyTreeNode<Location> lazyTreeNode, boolean b) {
	}

	@Override
	public Comparator<Location> getSortingComparator() {
		return DEFAULT_SORTING_COMPARATOR;
	}

	public static final Comparator<Location> DEFAULT_SORTING_COMPARATOR = (o1, o2) -> {
		if (o1 instanceof Region && o2 instanceof Street) {
			return -1;
		}
		if (o1 instanceof Street && o2 instanceof Region) {
			return 1;
		}
		if (o1 instanceof Region && o2 instanceof Region) {
			if (((Region) o1).getType().equals(((Region) o2).getType())) {
				return o1.getObjectName().compareTo(o2.getObjectName());
			}
		}
		if (o1 instanceof Street && o2 instanceof Street) {
			if (((Street) o1).getType().equals(((Street) o2).getType())) {
				return o1.getObjectName().compareTo(o2.getObjectName());
			}
		}
		return o1.getObjectName().compareTo(o2.getObjectName());
	};

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private String determineNodeType(Location address) {
		if (address instanceof Country) {
			return COUNTRY.getKeyword();
		}
		if (address instanceof Region) {
			return REGION.getKeyword();
		}
		if (address instanceof Street) {
			return STREET.getKeyword();
		}
		return DEFAULT_TYPE;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public LazyTreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(LazyTreeNode selectedNode) {
		this.selectedNode = selectedNode;
		setSelectedUnit((Location) selectedNode.getData());
	}

	public void setSelectedUnit(Location selectedUnit) {
		this.selectedUnit = selectedUnit;
	}
}