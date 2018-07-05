package ru.argustelecom.box.env.address;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Country;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.configuration.packages.Packages;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "addressBuildingsFM")
@PresentationModel
public class AddressBuildingsFrameModel implements Serializable {

	private static final long serialVersionUID = 7562557645653254487L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private AddressAppService addressAppSrv;

	@Inject
	private CurrentLocation currentLocation;

	private Location parent;

	private List<Building> buildings;
	private List<Building> selectedBuildings;

	public void preRender() {
		refresh();
	}

	public List<Building> getBuildings() {
		if (parent != null) {
			if (buildings == null) {
				buildings = em.createNamedQuery(Building.FIND_BUILDING_BY_PARENT, Building.class)
						.setParameter("parent", parent).getResultList();
			}
		}
		return buildings;
	}

	public void removeBuildings() {
		Iterator<Building> iterator = selectedBuildings.iterator();
		Building removingBuilding;
		while (iterator.hasNext()) {
			removingBuilding = iterator.next();
			em.remove(removingBuilding);
			em.flush();
			addressAppSrv.reindex(removingBuilding.getId());
			buildings.remove(removingBuilding);
		}
	}

	public boolean canCreateBuilding() {
		return parent != null && !(parent instanceof Country);
	}

	public Callback<Building> getBuildingCallback() {
		return (building -> buildings.add(building));
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		if (currentLocation.changed(parent)) {
			parent = currentLocation.getValue();
			buildings = null;
		}
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Location getParent() {
		return parent;
	}

	public List<Building> getSelectedBuildings() {
		return selectedBuildings;
	}

	public void setSelectedBuildings(List<Building> selectedBuildings) {
		this.selectedBuildings = selectedBuildings;
	}

	/**
	 * Есть ли возможность перехода в Тех.Учёт в паспорт дома
	 * @return истина если есть, иначе ложь
	 */
	public Boolean hasNriStructure() {
		return Packages.instance().isPackageDeployed(777L);
	}

}