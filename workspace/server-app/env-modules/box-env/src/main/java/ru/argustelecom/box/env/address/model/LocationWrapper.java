package ru.argustelecom.box.env.address.model;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.ILocation;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

@Named(value = ILocation.WRAPPER_NAME)
public class LocationWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Override
	public ILocation wrap(Identifiable entity) {
		checkNotNull(entity);
		Location location = (Location) entity;
		location = initializeAndUnproxy(location);

		//@formatter:off
		ILocation iLocation = ILocation.builder()
									.id(location.getId())
									.objectName(location.getObjectName())
									.fullName(location.getFullName())
								.build();
		//@formatter:on

		if (location instanceof Region)
			fillRegionData(iLocation, (Region) location);
		else if (location instanceof Street)
			fillStreetData(iLocation, (Street) location);
		else if (location instanceof Building)
			fillBuildingData(iLocation, (Building) location);
		else if (location instanceof Lodging)
			fillLodgingData(iLocation, (Lodging) location);
		else
			throw new SystemException(String.format("Unsupported location type: '%s'", location.getClass()));

		return iLocation;
	}

	@Override
	public Location unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(Location.class, iEntity.getId());
	}

	private void fillRegionData(ILocation iLocation, Region region) {
		iLocation.setRegionId(region.getId());
		iLocation.setRegionName(region.getName());
		iLocation.setRegionTreeName(region.getFullName());
	}

	private void fillStreetData(ILocation iLocation, Street street) {
		iLocation.setStreetId(street.getId());
		iLocation.setStreetName(street.getName());
		fillRegionData(iLocation, (Region) initializeAndUnproxy(street.getParent()));
	}

	private void fillBuildingData(ILocation iLocation, Building building) {
		iLocation.setBuildingId(building.getId());
		iLocation.setBuildingNumber(building.getNumber());
		iLocation.setBuildingCorpus(building.getCorpus());
		iLocation.setBuildingWing(building.getWing());
		iLocation.setBuildingName(building.getName());

		Location parent = initializeAndUnproxy(building.getParent());

		if (parent instanceof Region)
			fillRegionData(iLocation, (Region) parent);
		else if (parent instanceof Street)
			fillStreetData(iLocation, (Street) parent);
	}

	private void fillLodgingData(ILocation iLocation, Lodging lodging) {
		iLocation.setLodgingId(lodging.getId());
		iLocation.setLodgingName(lodging.getName());

		Location parent = initializeAndUnproxy(lodging.getParent());

		if (parent instanceof Region)
			fillRegionData(iLocation, (Region) parent);
		else if (parent instanceof Street)
			fillStreetData(iLocation, (Street) parent);
		else if (parent instanceof Building)
			fillBuildingData(iLocation, (Building) parent);
	}

	private static final long serialVersionUID = -2279897943703405385L;

}