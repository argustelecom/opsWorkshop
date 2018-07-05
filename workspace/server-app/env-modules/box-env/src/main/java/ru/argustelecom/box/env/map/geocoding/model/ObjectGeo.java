package ru.argustelecom.box.env.map.geocoding.model;

import lombok.Getter;
import lombok.Setter;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import ru.argustelecom.system.inf.map.crs.CrsUtils;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(schema = "system", name = "object_geo")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "geo_type")
@Access(AccessType.FIELD)
@Getter
@Setter
@NamedQueries({
		//@NamedQuery(name = ObjectGeo.FIND_NEAREST_ANY_ENT_QUERY, query = "from ObjectGeo g where g.key.mapId = :mapId and nearest(g.location, :center, :range, 1) = 'TRUE' order by nn_dist(1)"),
		//@NamedQuery(name = ObjectGeo.FIND_NEAREST_QUERY, query = "from ObjectGeo g where g.key.mapId = :mapId and nearest(g.location, :center, :range, 1) = 'TRUE' and in_ex(g.object.entityId, :entityIds) = 1 order by nn_dist(1)"),
		//@NamedQuery(name = ObjectGeo.FIND_BY_OBJECTS_QUERY, query = "from ObjectGeo g where g.key.mapId = :mapId and in_ex(g.key.objectId, :objectIds) = 1")
})
@NamedNativeQuery(name = ObjectGeo.GET_REGION_BY_POSITION_QUERY, query = "SELECT pkg_object_geo.get_region_by_longlat(:lng, :lat, :error_if_not_found) FROM DUAL")
public class ObjectGeo implements Serializable {

	private static final long serialVersionUID = -1;

	public static final String FIND_BY_OBJECTS_QUERY = "ObjectGeo.FindByObjects";
	public static final String FIND_NEAREST_QUERY = "ObjectGeo.FindNearest";
	public static final String FIND_NEAREST_ANY_ENT_QUERY = "ObjectGeo.FindNearestAnyEnt";
	public static final String GET_REGION_BY_POSITION_QUERY = "ObjectGeo.getRegionByPosition";

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "map_id")
	private Long mapId;

	@Column(name = "location", columnDefinition = "geometry(Point,4326)")
	private Point<G2D> location;

	@Column(name = "shape", columnDefinition = "geometry()")
	private Geometry<G2D> shape;

	protected ObjectGeo() {
	}

	public ObjectGeo(G2D posLngLat, Long mapId) {
		this.mapId = mapId;
		this.location = new Point<>(posLngLat, CrsUtils.WGS84_CRS);
	}

	public G2D getPosition() {
		return location.getPosition();
	}

	public void setPosition(@NotNull G2D value) {
		location = new Point<>(value, CrsUtils.WGS84_CRS);
	}

	public Envelope<G2D> getMbr() {
		if (shape == null) {
			return null;
		}
		return shape.getEnvelope();
	}
}
