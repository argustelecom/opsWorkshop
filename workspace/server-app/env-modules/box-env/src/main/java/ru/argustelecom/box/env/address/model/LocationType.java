package ru.argustelecom.box.env.address.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

/**
 * Справочник типов адресообразующих элементов.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", uniqueConstraints = {
		@UniqueConstraint(name = "unq_location_type", columnNames = { "level_id", "name" }) })
//@formatter:off
@NamedQueries(value = {
		@NamedQuery(name = LocationType.GET_ALL_TYPES, query = "select lt from LocationType lt"),
		@NamedQuery(name = LocationType.FIND_TYPES_BY_LEVEL, query = "select lt from LocationType lt where lt.level = :" + LocationType.LEVEL_QUERY_PARAM)
})
//@formatter:on
public class LocationType extends BusinessDirectory {

	private static final long serialVersionUID = -2936084923580752632L;

	public static final String GET_ALL_TYPES = "LocationType.getAllTypes";
	public static final String FIND_TYPES_BY_LEVEL = "LocationType.findTypesByLevel";
	public static final String LEVEL_QUERY_PARAM = "level";

	@Column(nullable = false)
	private boolean sys;

	@Column(length = 64, nullable = false)
	private String name;

	@Column(length = 16)
	private String shortName;

	@ManyToOne(fetch = FetchType.LAZY)
	private LocationLevel level;

	protected LocationType() {
	}

	public LocationType(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return name;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public boolean isSys() {
		return sys;
	}

	public void setSys(boolean sys) {
		this.sys = sys;
	}

	@Override
	public Boolean getIsSys() {
		return sys;
	}

	/**
	 * @return Наименование типа адресообразующего элемента.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Сокращенное наименование адресообразующего элемента.
	 */
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return Уровень типа адресообразующего элемента.
	 */
	public LocationLevel getLevel() {
		return level;
	}

	public void setLevel(LocationLevel level) {
		this.level = level;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class LocationTypeQuery extends EntityQuery<LocationType> {

		EntityQueryStringFilter<LocationType> name = createStringFilter(LocationType_.name);
		EntityQueryStringFilter<LocationType> shortName = createStringFilter(LocationType_.shortName);
		EntityQueryEntityFilter<LocationType, LocationLevel> level = createEntityFilter(LocationType_.level);
		EntityQuerySimpleFilter<LocationType, Boolean> sys = createFilter(LocationType_.sys);

		public LocationTypeQuery() {
			super(LocationType.class);
		}

		public EntityQueryStringFilter<LocationType> name() {
			return name;
		}

		public EntityQueryStringFilter<LocationType> shortName() {
			return shortName;
		}

		public EntityQueryEntityFilter<LocationType, LocationLevel> level() {
			return level;
		}

		public EntityQuerySimpleFilter<LocationType, Boolean> sys() {
			return sys;
		}

	}

}