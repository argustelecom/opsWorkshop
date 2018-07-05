package ru.argustelecom.box.env.address;

import static ru.argustelecom.box.env.address.model.Location.PARENT_QUERY_PARAM;

import java.util.List;

import javax.persistence.EntityManager;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.box.env.address.model.Street;
import ru.argustelecom.system.inf.dataloader.LazyTreeNodeLoader;

public class AddressLazyTreeNodeLoader implements LazyTreeNodeLoader<Location> {

	private EntityManager em;

	public AddressLazyTreeNodeLoader(EntityManager em) {
		this.em = em;
	}

	@Override
	public List<Location> loadChildren(Location region) {
		List<Location> childLocations = em.createNamedQuery(Region.FIND_CHILD_REGIONS, Location.class)
				.setParameter(PARENT_QUERY_PARAM, region).getResultList();
		childLocations.addAll(em.createNamedQuery(Street.GET_STREETS_BY_REGION, Location.class)
				.setParameter(PARENT_QUERY_PARAM, region).getResultList());
		return childLocations;
	}

}