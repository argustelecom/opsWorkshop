package ru.argustelecom.box.env.address.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * Адресообразующие элементы, которые являются регионами.
 */
@Entity
@Access(AccessType.FIELD)
//@formatter:off
@NamedQueries(value = {
		@NamedQuery(name = Region.FIND_ROOT_REGIONS, query = "select r from Region r where r.type.level = 0"),
		@NamedQuery(name = Region.FIND_CHILD_REGIONS, query = "select r from Region r where r.parent = :" + Location.PARENT_QUERY_PARAM)
})
//@formatter:on
public class Region extends Location implements RegionContainer, StreetContainer, LodgingContainer {

	private static final long serialVersionUID = -6695766141143641081L;

	public static final String FIND_ROOT_REGIONS = "Region.findRootRegions";
	public static final String FIND_CHILD_REGIONS = "Region.findChildRegions";

	@ManyToOne(fetch = FetchType.LAZY)
	private LocationType type;

	protected Region() {
	}

	public Region(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return String.format("%s %s", getName(), type.getShortName());
	}

	@Override
	public AddressRdo createReportData() {
		AddressRdo addressRdo = super.createReportData();
		fill(addressRdo);
		return addressRdo;
	}

	@Override
	public void addChild(Region region) {
		super.addChild(region);
	}

	@Override
	public void addChild(Street street) {
		super.addChild(street);
	}

	@Override
	public void addChild(Lodging lodging) {
		super.addChild(lodging);
	}

	@Override
	public void removeChild(Region region) {
		super.removeChild(region);
	}

	@Override
	public void removeChild(Street street) {
		super.removeChild(street);
	}

	@Override
	public void removeChild(Lodging lodging) {
		super.removeChild(lodging);
	}

	protected void fill(AddressRdo addressRdo) {
		addressRdo.setRegionName(getObjectName());
		addressRdo.setRegionTreeName(getFullName());
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	/**
	 * @return Тип региона.
	 */
	public LocationType getType() {
		return type;
	}

	public void setType(LocationType type) {
		this.type = type;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class RegionQuery extends LocationQuery<Region> {

		EntityQueryEntityFilter<Region, LocationType> type = createEntityFilter(Region_.type);

		public RegionQuery() {
			super(Region.class);
		}

		public EntityQueryEntityFilter<Region, LocationType> type() {
			return type;
		}

	}

}