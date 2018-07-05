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
 * Адресообразующие элементы, которые являются улицами.
 */
@Entity
@Access(AccessType.FIELD)
//@formatter:off
@NamedQueries(value = {
		@NamedQuery(name = Street.GET_STREETS_BY_REGION, query = "select s from Street s where s.parent = :" + Location.PARENT_QUERY_PARAM)
})
//@formatter:on
public class Street extends Location implements LodgingContainer {

	private static final long serialVersionUID = 5049606493796709997L;

	public static final String GET_STREETS_BY_REGION = "Street.getStreetsByRegion";

	@ManyToOne(fetch = FetchType.LAZY)
	private LocationType type;

	protected Street() {
	}

	public Street(Long id) {
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
	public void addChild(Lodging lodging) {
		super.addChild(lodging);
	}

	@Override
	public void removeChild(Lodging lodging) {
		super.removeChild(lodging);
	}

	protected void fill(AddressRdo addressRdo) {
		addressRdo.setRegionName(getParent().getObjectName());
		addressRdo.setRegionTreeName(getParent().getFullName());
		addressRdo.setStreetName(getObjectName());
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	/**
	 * @return тип улицы.
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

	public static class StreetQuery extends LocationQuery<Street> {

		EntityQueryEntityFilter<Street, LocationType> type = createEntityFilter(Street_.type);

		public StreetQuery() {
			super(Street.class);
		}

		public EntityQueryEntityFilter<Street, LocationType> type() {
			return type;
		}

	}

}