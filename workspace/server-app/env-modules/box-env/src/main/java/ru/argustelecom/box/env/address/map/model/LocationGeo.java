package ru.argustelecom.box.env.address.map.model;

import lombok.Getter;
import lombok.Setter;
import org.geolatte.geom.G2D;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.map.geocoding.model.ObjectGeo;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@DiscriminatorValue("location")
@Access(AccessType.FIELD)
@Getter
@Setter
@NamedQueries({
		@NamedQuery(name = LocationGeo.FIND_NEAREST_QUERY,
				query = "from LocationGeo g where g.mapId = :mapId and within_meters(g.location, :center, :range)  = 'TRUE' " +
						"order by nearest_to(g.location, :center)")
})
public class LocationGeo extends ObjectGeo {

	public static final String FIND_NEAREST_QUERY = "LocationGeo.FindNearestAnyEnt";

	@OneToOne()
	@JoinColumn(name = "location_id")
	private Location locationAddr;

	protected LocationGeo() {
		super();
	}


	public LocationGeo(Location locationAddr, G2D posLngLat, Long mapId) {
		super(posLngLat, mapId);
		this.locationAddr = locationAddr;
	}
}
