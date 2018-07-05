package ru.argustelecom.box.env.address.map;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import ru.argustelecom.box.env.address.LocationRepository;
import ru.argustelecom.box.env.address.map.model.LocationGeo;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.map.geocoding.SpecializedObjectGeoRepository;
import ru.argustelecom.box.env.map.geocoding.model.ObjectGeo;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.map.crs.CrsUtils;
import ru.argustelecom.system.inf.modelbase.SuperClass;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Repository
public class LocationGeoRepository implements SpecializedObjectGeoRepository, Serializable {

	private static final long serialVersionUID = 4956460714740882556L;

	private static final Double DEFAULT_SEARCH_RANGE = 100D;

	@PersistenceContext
	private EntityManager em;

	public LocationGeo findById(Long id) {
		return em.find(LocationGeo.class, id);
	}

	public LocationGeo findByLocation(Location location, Long mapId) {
		Query query = em.createQuery("from LocationGeo lg where lg.locationAddr = :addr and lg.mapId = :mapId"
				, LocationGeo.class);

		query.setParameter("addr", location);
		query.setParameter("mapId", mapId);

		return (LocationGeo)query.getResultList().stream().findFirst().orElse(null);
	}

	public LocationGeo createGeoLocation(Location location, G2D position, Long mapId) {
		LocationGeo locationGeo = new LocationGeo(location, position, mapId);
		em.persist(locationGeo);
		return locationGeo;
	}

	public LocationGeo updatePosition(LocationGeo buildingGeo, G2D position) {
		LocationGeo lg = findById(buildingGeo.getId());
		lg.setPosition(position);
		em.merge(lg);
		return lg;
	}

	@Override
	public LocationGeo findByObject(SuperClass object, Long mapId) {
		return em.createQuery("from LocationGeo lg where lg.locationAddr = :location", LocationGeo.class)
				.setParameter("location",object)
				.getResultList().stream().findAny().orElse(null);
	}

	@Override
	public boolean objectSupported(SuperClass object) {
		return object instanceof Location;
	}

	@Override
	public BusinessObject findNearest(Long mapId, G2D position) {

		Double range = DEFAULT_SEARCH_RANGE;

		TypedQuery<LocationGeo> q;

		q = em.createNamedQuery(LocationGeo.FIND_NEAREST_QUERY, LocationGeo.class);

		q.setParameter("mapId", mapId);
		q.setParameter("center", new Point<>(position, CrsUtils.WGS84_CRS));
		q.setParameter("range", range);
		q.setMaxResults(1);

		List<LocationGeo> result = q.getResultList();
		return result.isEmpty() ? null : result.get(0).getLocationAddr();
	}

	@Override
	public boolean supportsAnyClass(Set<Class> classes) {
		return classes.stream()
				.filter(cls -> Location.class.isAssignableFrom(cls))
				.findAny()
				.isPresent();
	}
}
