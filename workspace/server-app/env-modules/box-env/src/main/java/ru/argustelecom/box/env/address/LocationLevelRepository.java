package ru.argustelecom.box.env.address;

import static ru.argustelecom.box.env.address.model.LocationLevel.BUILDING;
import static ru.argustelecom.box.env.address.model.LocationLevel.COUNTRY_SUBJECT;
import static ru.argustelecom.box.env.address.model.LocationLevel.DISTRICT;
import static ru.argustelecom.box.env.address.model.LocationLevel.LODGING;
import static ru.argustelecom.box.env.address.model.LocationLevel.STREET;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.address.model.LocationLevel.LocationLevelQuery;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class LocationLevelRepository implements Serializable {

	private static final long serialVersionUID = 4956460714740882556L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public LocationLevel createLevel(@NotNull String name) {
		LocationLevel newLevel = new LocationLevel(idSequence.nextValue(LocationLevel.class));
		newLevel.setName(name);
		em.persist(newLevel);
		return newLevel;
	}

	public LocationLevel countrySubject() {
		return em.find(LocationLevel.class, COUNTRY_SUBJECT);
	}

	public LocationLevel populatedLocality() {
		return em.find(LocationLevel.class, LocationLevel.POPULATED_LOCALITY);
	}

	public LocationLevel district() {
		return em.find(LocationLevel.class, DISTRICT);
	}

	public LocationLevel street() {
		return em.find(LocationLevel.class, STREET);
	}

	public LocationLevel building() {
		return em.find(LocationLevel.class, BUILDING);
	}

	public LocationLevel lodging() {
		return em.find(LocationLevel.class, LODGING);
	}

	public List<LocationLevel> findAllLevels() {
		return new LocationLevelQuery().createTypedQuery(em).getResultList();
	}

	public List<LocationLevel> findRegionLevels() {
		List<Long> notRegionLevelsId = Lists.newArrayList(DISTRICT, STREET, BUILDING, LODGING);
		List<LocationLevel> allLevels = new LocationLevelQuery().createTypedQuery(em).getResultList();
		return allLevels.stream().filter(level -> !notRegionLevelsId.contains(level.getId()))
				.collect(Collectors.toList());
	}

}