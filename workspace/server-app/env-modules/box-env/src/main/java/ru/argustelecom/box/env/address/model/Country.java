package ru.argustelecom.box.env.address.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Адресообразующие элементы, которые являются страной.
 */
@Entity
@Access(AccessType.FIELD)
//@formatter:off
@NamedQueries(value = {
		@NamedQuery(name = Country.GET_ALL_COUNTRIES, query = "select c from Country c")
})
//@formatter:on
public class Country extends Location implements DistrictContainer, RegionContainer {

	private static final long serialVersionUID = 5088524931319473080L;

	public static final String GET_ALL_COUNTRIES = "Country.getAllCountries";

	protected Country() {
	}

	public Country(Long id) {
		super(id);
	}

	@Override
	public void addChild(District district) {
		super.addChild(district);
	}

	@Override
	public void addChild(Region region) {
		super.addChild(region);
	}

	@Override
	public void removeChild(District district) {
		super.removeChild(district);
	}

	@Override
	public void removeChild(Region region) {
		super.removeChild(region);
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class CountryQuery extends LocationQuery<Country> {

		public CountryQuery() {
			super(Country.class);
		}

	}

}