package ru.argustelecom.box.env.address.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * Адресообразующие элементы, которые являются районами.
 */
@Entity
@Access(AccessType.FIELD)
public class District extends Location implements DistrictContainer {

	private static final long serialVersionUID = 9162973982170107375L;

	@ManyToOne(fetch = FetchType.LAZY)
	private LocationType type;

	protected District() {
	}

	@Override
	public void addChild(District district) {
		super.addChild(district);
	}

	@Override
	public void removeChild(District district) {
		super.addChild(district);
	}

	@Override
	public String getObjectName() {
		return String.format("%s %s", getName(), type.getShortName());
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	/**
	 * @return Тип района.
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

	public static class DistrictQuery extends LocationQuery<District> {

		EntityQueryEntityFilter<District, LocationType> type = createEntityFilter(District_.type);

		public DistrictQuery() {
			super(District.class);
		}

		public EntityQueryEntityFilter<District, LocationType> type() {
			return type;
		}

	}

}