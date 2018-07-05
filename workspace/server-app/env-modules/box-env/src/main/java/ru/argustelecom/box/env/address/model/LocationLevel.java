package ru.argustelecom.box.env.address.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", uniqueConstraints = {
		@UniqueConstraint(name = "unq_location_level", columnNames = { "name" }) })
public class LocationLevel extends BusinessDirectory {

	private static final long serialVersionUID = 1164097129542913598L;

	public static final Long COUNTRY_SUBJECT = 1L;
	public static final Long POPULATED_LOCALITY = 2L;
	public static final Long DISTRICT = 3L;
	public static final Long STREET = 4L;
	public static final Long BUILDING = 5L;
	public static final Long LODGING = 6L;

	public static final String GET_ALL_LEVELS = "LocationLevel.getAllLevels";

	@Column(length = 64, nullable = false)
	public String name;

	@Column(nullable = false)
	public boolean sys;

	protected LocationLevel() {
	}

	public LocationLevel(Long id) {
		super(id);
		this.sys = false;
	}

	// *****************************************************************************************************************
	// Service methods
	// *****************************************************************************************************************

	@Override
	public String getObjectName() {
		return name;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSys() {
		return sys;
	}

	public void setSys(boolean sys) {
		this.sys = sys;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class LocationLevelQuery extends EntityQuery<LocationLevel> {

		EntityQueryStringFilter<LocationLevel> name = createStringFilter(LocationLevel_.name);
		EntityQuerySimpleFilter<LocationLevel, Boolean> sys = createFilter(LocationLevel_.sys);

		public LocationLevelQuery() {
			super(LocationLevel.class);
		}

		public EntityQueryStringFilter<LocationLevel> name() {
			return name;
		}

		EntityQuerySimpleFilter<LocationLevel, Boolean> sys() {
			return sys;
		}

	}

}