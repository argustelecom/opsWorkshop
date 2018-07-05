package ru.argustelecom.box.env.address;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Building.BuildingQuery;
import ru.argustelecom.box.env.address.model.Country;
import ru.argustelecom.box.env.address.model.Country.CountryQuery;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.address.model.Location_;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.box.env.address.model.Region.RegionQuery;
import ru.argustelecom.box.env.address.model.Street;
import ru.argustelecom.box.env.address.model.Street.StreetQuery;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

@Repository
public class LocationRepository implements Serializable {

	private static final long serialVersionUID = -95940289441136358L;

	private static final String REINDEX = "LocationRepository.reindex";
	private static final String FULL_REINDEX = "LocationRepository.fullReindex";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public Country createCountry(String name) {
		Country newCountry = new Country(idSequence.nextValue(Country.class));
		newCountry.setName(name);
		em.persist(newCountry);
		return newCountry;
	}

	public Region createRegion(Location parent, @NotNull String name, @NotNull LocationType type) {
		Region newRegion = new Region(idSequence.nextValue(Region.class));
		newRegion.setParent(parent);
		newRegion.setName(name);
		newRegion.setType(type);
		em.persist(newRegion);
		return newRegion;
	}

	public Street createStreet(Location parent, @NotNull String name, @NotNull LocationType type) {
		Street newStreet = new Street(idSequence.nextValue(Street.class));
		newStreet.setParent(parent);
		newStreet.setName(name);
		newStreet.setType(type);
		em.persist(newStreet);
		return newStreet;
	}

	public Building createBuilding(Location parent, @NotNull String number, String corpus, String wing,
			String postIndex, String landmark) {
		Building newBuilding = new Building(idSequence.nextValue(Building.class));
		newBuilding.setParent(parent);
		newBuilding.setNumber(number);
		newBuilding.setCorpus(corpus);
		newBuilding.setWing(wing);
		newBuilding.setPostIndex(postIndex);
		newBuilding.setLandmark(landmark);
		newBuilding.setName(number);

		em.persist(newBuilding);
		return newBuilding;
	}

	private Lodging createLodging(@NotNull Location parent, @NotNull LocationType type, @NotNull String number) {
		Lodging newLodging = new Lodging(idSequence.nextValue(Lodging.class));
		newLodging.setParent(parent);
		newLodging.setType(type);
		newLodging.setNumber(number);
		newLodging.setName(format("%s %s", parent.getName(), number));
		em.persist(newLodging);
		return newLodging;
	}

	public Country findCountry(@NotNull String name) {
		try {
			CountryQuery query = new CountryQuery();

			//@formatter:off
			query.and(
				query.name().equalIgnoreCase(name)
			);
			//@formatter:on

			return query.createTypedQuery(em).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public Region findRegion(Location location, @NotNull LocationType type, @NotNull String name) {
		try {
			RegionQuery query = new RegionQuery();

			//@formatter:off
			query.and(
					query.parent().equal(location),
					query.type().equal(type),
					query.name().equalIgnoreCase(name)
			);
			//@formatter:on

			return query.createTypedQuery(em).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public Street findStreet(Location location, @NotNull LocationType type, @NotNull String name) {
		try {
			StreetQuery query = new StreetQuery();

			//@formatter:off
			query.and(
				query.parent().equal(location),
				query.type().equal(type),
				query.name().equalIgnoreCase(name)
			);
			//@formatter:on

			return query.createTypedQuery(em).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public Building findBuilding(@NotNull Location parent, @NotNull String number, String corpus, String wing) {
		try {
			BuildingQuery query = new BuildingQuery();

			//@formatter:off
			query.and(
				query.parent().equal(parent),
				query.number().equalIgnoreCase(number)
			);
			//@formatter:on

			if (corpus != null)
				query.and(query.corpus().equalIgnoreCase(corpus));
			else
				query.and(query.corpus().isNull());

			if (wing != null)
				query.and(query.wing().equalIgnoreCase(wing));
			else
				query.and(query.wing().isNull());

			return query.createTypedQuery(em).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public Lodging findOrCreateLodging(@NotNull Location parent, @NotNull LocationType type, @NotNull String number) {
		Lodging lodging = findLodging(parent, type, number);
		return lodging != null ? lodging : createLodging(parent, type, number);
	}

	public List<Building> findAllBuildings() {
		return new BuildingQuery().createTypedQuery(em).getResultList();
	}

	public Lodging findLodging(@NotNull Location parent, @NotNull LocationType type, @NotNull String number) {
		try {
			Lodging.LodgingQuery query = new Lodging.LodgingQuery();

			//@formatter:off
			query.and(
				query.parent().equal(parent),
				query.type().equal(type),
				query.number().equal(number)
			);
			//@formatter:on

			return query.createTypedQuery(em).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/**
	 * получает location'ы по очереди элементов адреса
	 *
	 * @param locationPath очередь элементов адреса (например: Санкт-Петербург, курсанта, 25)
	 * @param maxResCount максимальное количество результатов
	 * @return список location'ов
	 */
	public List<Location> searchLocationsLike(Deque<String> locationPath, int maxResCount) {
		if (isEmpty(locationPath))
			return emptyList();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Location> query = cb.createQuery(Location.class);
		Root<Location> rootLocation = query.from(Location.class);

		Predicate locationLikeEntered = createLocationLikePredicate(cb, rootLocation, locationPath);

		query.where(locationLikeEntered);
		return em.createQuery(query).setMaxResults(maxResCount).getResultList();
	}

	@NamedNativeQuery(name = REINDEX, query = "SELECT address_search.reindex(:locationId)")
	public Boolean reindex(Long locationId) {
		return (Boolean) em.createNamedQuery(REINDEX).setParameter("locationId", locationId)
				.getSingleResult();
	}

	@NamedNativeQuery(name = FULL_REINDEX, query = "SELECT address_search.reindex_full()")
	public Boolean fullReindex() {
		return (Boolean) em.createNamedQuery(FULL_REINDEX).getSingleResult();
	}

	/**
	 * создает предикат для поиска ресурса по адресу
	 * @param cb строитель критериев
	 * @param root путь к рутовому элементу
	 * @param locationPath очередь элементов адреса
	 * @return предикат
	 */
	private Predicate createLocationLikePredicate(CriteriaBuilder cb, From<Location, Location> root,
			Deque<String> locationPath) {

		if (isEmpty(locationPath))
			return null;

		List<Predicate> predicates = new ArrayList<>();

		From<Location, Location> parentLocation = root;
		Predicate rootNameEqual = like(cb, root.get("name"), locationPath.pollLast());
		predicates.add(rootNameEqual);

		while (!isEmpty(locationPath)) {
			parentLocation = parentLocation.join(Location_.parent, JoinType.LEFT);
			Predicate parentNameEqual = like(cb, parentLocation.get("name"), locationPath.pollLast());
			predicates.add(parentNameEqual);
		}
		return cb.and(predicates.toArray(new Predicate[0]));
	}

	/**
	 * создает like предикат
	 * @param cb строитель критериев
	 * @param fieldPath путь к проверяемому полю
	 * @param likeThis стринга, с которой сравниваем поле
	 * @return предикат
	 */
	private Predicate like(CriteriaBuilder cb, Path<String> fieldPath, String likeThis) {
		if (StringUtils.isEmpty(likeThis))
			return null;
		return cb.like(cb.lower(fieldPath), lowerCase("%" + likeThis + "%"));
	}

	public List<Lodging> findAllLodgingsByBuilding(Location parent) {
		try {
			Lodging.LodgingQuery query = new Lodging.LodgingQuery();

			//@formatter:off
			query.and(
					query.parent().equal(parent)
			);
			//@formatter:on

			return query.createTypedQuery(em).getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

}