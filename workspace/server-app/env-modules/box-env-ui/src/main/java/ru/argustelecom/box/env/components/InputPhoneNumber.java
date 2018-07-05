package ru.argustelecom.box.env.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@FacesComponent("inputPhoneNumber")
public class InputPhoneNumber extends UINamingContainer {

	private static final String ID_ATTR = "id";
	private static final String PREFERRED_ATTR = "preferred";
	private static final String ONLY_COUNTRIES_ATTR = "onlyCountries";
	private static final String COUNTRY_UNIONS_ATTR = "countryUnions";

	private String id;
	private List<String> preferredCountries;
	private List<String> onlyCountries;
	private List<CountryUnion> countryUnions;

	@SuppressWarnings("unchecked")
	public void init() {
		id = (String) getAttributes().get(ID_ATTR);
		preferredCountries = splitStringToListValues((String) getAttributes().get(PREFERRED_ATTR));
		onlyCountries = splitStringToListValues((String) getAttributes().get(ONLY_COUNTRIES_ATTR));
		countryUnions = (ArrayList<CountryUnion>) getAttributes().get(COUNTRY_UNIONS_ATTR);
	}

	public String getLabel() {
		return LocaleUtils.getBundle("InputPhoneNumberBundle", getClass()).getString("box.input.phone.number");
	}

	public String getCountries() {
		if (onlyCountries != null && !onlyCountries.isEmpty())
			return aggregateToStringValue(Sets.newHashSet(onlyCountries));

		if (countryUnions == null || countryUnions.isEmpty())
			return StringUtils.EMPTY;

		Set<String> countries = new HashSet<>();
		countryUnions.forEach(countryUnion -> countries.addAll(countryUnion.getCountries()));

		return aggregateToStringValue(countries);
	}

	public String getPreferredCountries() {
		return preferredCountries != null ? aggregateToStringValue(Sets.newHashSet(preferredCountries))
				: StringUtils.EMPTY;
	}

	public String getFullId() {
		return String.format("#%s-%s", getClientId(), id);
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private String aggregateToStringValue(Set<String> values) {
		if (values == null || values.isEmpty())
			return StringUtils.EMPTY;

		StringBuilder value = new StringBuilder();
		values.forEach(v -> value.append("\"").append(v).append("\", "));
		return value.substring(0, value.toString().length() - 2);
	}

	private List<String> splitStringToListValues(String rawString) {
		if (rawString == null || rawString.isEmpty())
			return Collections.emptyList();

		return Arrays.stream(rawString.split(",")).map(v -> v.toLowerCase().trim()).collect(Collectors.toList());
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public enum CountryUnion {
		//@formatter:off
		CIS	(Sets.newHashSet("az", "am", "by", "kz", "md", "ru", "tj", "tm", "uz", "ua")),

		// EUROPE
		W_EUROPE	(Sets.newHashSet("at", "be", "gb", "de", "ie", "li", "lu", "mc", "nl", "fr", "ch")),
		E_EUROPE	(Sets.newHashSet("by", "bg", "hu", "md", "pl", "ru", "ro", "sk", "cz", "ua")),
		N_EUROPE	(Sets.newHashSet("dk", "is", "lv", "lt", "no", "fi", "ee", "se")),
		S_EUROPE	(Sets.newHashSet("al", "ad", "ba", "va", "gr", "es", "it", "mk", "mt", "pt", "sm", "rs", "si", "hr", "me")),

		// ASIAN
		W_ASIAN		(Sets.newHashSet("az", "am", "bh", "ge", "il", "jo", "iq", "ir", "ye", "qa", "cy", "kw", "lb", "ae", "om", "sa", "sy", "tr")),
		E_ASIAN		(Sets.newHashSet("cn", "kp", "kr", "mn", "jp")),
		SE_ASIAN	(Sets.newHashSet("bn", "vn", "id", "kh", "la", "my", "mm", "sg", "th", "tl", "ph")),
		S_ASIAN		(Sets.newHashSet("bd", "in", "mv", "np", "pk", "lk")),
		C_ASIAN		(Sets.newHashSet("kz", "kg", "tj", "tm", "uz", "af")),

		// AMERICA
		N_AMERICA	(Sets.newHashSet("ca", "us", "mx")),
		С_AMERICA	(Sets.newHashSet("bz", "gt", "hn", "cr", "ni", "pa", "sv")),
		CP_AMERICA	(Sets.newHashSet("ag", "bs", "bb", "ht", "gd", "dm", "do", "cu", "vc", "kn", "lc", "tt", "jm")),
		S_AMERICA	(Sets.newHashSet("ar", "bo", "br", "ve", "gy", "co", "py", "pe", "sr", "uy", "cl", "ec"));

		//@formatter:on

		private Set<String> countries;

		CountryUnion(Set<String> countries) {
			this.countries = countries;
		}

		public Set<String> getCountries() {
			return countries;
		}

	}

}