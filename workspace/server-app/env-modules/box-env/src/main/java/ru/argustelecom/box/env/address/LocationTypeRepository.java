package ru.argustelecom.box.env.address;

import static ru.argustelecom.box.env.address.model.LocationType.LocationTypeQuery;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class LocationTypeRepository implements Serializable {

	private static final long serialVersionUID = 7015916354212084480L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private LocationLevelRepository locationLevelRepository;

	public LocationType createRegionType(@NotNull LocationLevel level, @NotNull String name, String shortName) {
		LocationType newRegionType = createType(level, name, shortName);
		em.persist(newRegionType);
		return newRegionType;
	}

	public LocationType createDistrictType(@NotNull String name, String shortName) {
		LocationType newDistrictType = createType(locationLevelRepository.district(), name, shortName);
		em.persist(newDistrictType);
		return newDistrictType;
	}

	public LocationType createStreetType(@NotNull String name, String shortName) {
		LocationType newStreetType = createType(locationLevelRepository.street(), name, shortName);
		em.persist(newStreetType);
		return newStreetType;
	}

	public LocationType createBuildingType(@NotNull String name, String shortName) {
		LocationType newBuildingType = createType(locationLevelRepository.building(), name, shortName);
		em.persist(newBuildingType);
		return newBuildingType;
	}

	public LocationType createLodgingType(@NotNull String name, String shortName) {
		LocationType newLodgingType = createType(locationLevelRepository.lodging(), name, shortName);
		em.persist(newLodgingType);
		return newLodgingType;
	}

	public List<LocationType> findLocationTypes(LocationLevel level) {
		LocationTypeQuery query = new LocationTypeQuery();
		query.and(query.level().equal(level));
		return query.createTypedQuery(em).getResultList();
	}

	public List<LocationType> findLodgingTypes() {
		return findLocationTypes(locationLevelRepository.lodging());
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private LocationType createType(LocationLevel level, String name, String shortName) {
		LocationType newRegionType = new LocationType(idSequence.nextValue(LocationType.class));
		newRegionType.setLevel(level);
		newRegionType.setName(name);
		newRegionType.setShortName(shortName);
		return newRegionType;
	}

}