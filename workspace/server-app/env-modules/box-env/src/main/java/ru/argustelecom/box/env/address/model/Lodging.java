package ru.argustelecom.box.env.address.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

/**
 * Адресообразующие элементы, которые являются квартирами/помещениями.
 */
@Entity
@Access(AccessType.FIELD)
public class Lodging extends Location {

	private static final long serialVersionUID = 5911260216336447762L;

	@ManyToOne
	private LocationType type;

	@Column(nullable = false, length = 16)
	private String number;

	protected Lodging() {
	}

	public Lodging(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return String.format("%s %s", number, type.getShortName());
	}

	@Override
	public AddressRdo createReportData() {
		AddressRdo addressRdo = super.createReportData();

		Location parent = EntityManagerUtils.initializeAndUnproxy(getParent());

		if (parent instanceof Region) {
			((Region) parent).fill(addressRdo);
		}
		if (parent instanceof Street) {
			((Street) parent).fill(addressRdo);
		}
		if (parent instanceof Building) {
			((Building) parent).fill(addressRdo);
		}

		addressRdo.setLodgingName(getObjectName());

		return addressRdo;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	/**
	 * @return Тип квартиры/помещения.
	 */
	public LocationType getType() {
		return type;
	}

	public void setType(LocationType type) {
		this.type = type;
	}

	/**
	 * @return Номер квартиры/помещения.
	 */
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class LodgingQuery extends LocationQuery<Lodging> {

		EntityQueryEntityFilter<Lodging, LocationType> type = createEntityFilter(Lodging_.type);
		EntityQueryStringFilter<Lodging> number = createStringFilter(Lodging_.number);

		public LodgingQuery() {
			super(Lodging.class);
		}

		public EntityQueryEntityFilter<Lodging, LocationType> type() {
			return type;
		}

		public EntityQueryStringFilter<Lodging> number() {
			return number;
		}

	}

}