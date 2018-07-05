package ru.argustelecom.box.env.address.model;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.commons.lang3.StringUtils;

import ru.argustelecom.box.env.address.nls.LocationMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

/**
 * Адресообразующие элементы, которые являются зданиями.
 */
@Entity
@Access(AccessType.FIELD)
//@formatter:off
@NamedQueries(value = {
		@NamedQuery(name = Building.FIND_BUILDING_BY_PARENT, query = "select b from Building b where b.parent = :" + Location.PARENT_QUERY_PARAM)
})
//@formatter:on
public class Building extends Location implements LodgingContainer {

	private static final long serialVersionUID = 119409928727909818L;

	public static final String FIND_BUILDING_BY_PARENT = "Building.findBuildingByParent";

	@Column(length = 64, nullable = false)
	private String number;

	@Column(length = 10)
	private String corpus;

	@Column(length = 10)
	private String wing;

	@Column(length = 32)
	private String postIndex;

	protected Building() {
	}

	public Building(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		LocationMessagesBundle messages = LocaleUtils.getMessages(LocationMessagesBundle.class);
		StringBuilder name = new StringBuilder();
		name.append(number);
		if (!StringUtils.isEmpty(corpus))
			name.append(String.format(" %s %s", corpus, messages.corpusShortName()));
		if (!StringUtils.isEmpty(wing))
			name.append(String.format(" %s %s", wing, messages.wingShortName()));
		return name.toString();
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

	@Override
	public void setName(String number) {
		StringBuilder builder = new StringBuilder();
		builder.append(number);

		LocationMessagesBundle messages = getMessages(LocationMessagesBundle.class);
		if (nonNull(corpus))
			builder.append(format(" %s %s", messages.corpusShortName(), corpus));
		if (nonNull(wing))
			builder.append(format(" %s %s", messages.wingShortName(), wing));
		super.setName(builder.toString());
	}

	protected void fill(AddressRdo addressRdo) {
		Location parent = EntityManagerUtils.initializeAndUnproxy(getParent());

		if (parent instanceof Region) {
			((Region) parent).fill(addressRdo);
		}
		if (parent instanceof Street) {
			((Street) parent).fill(addressRdo);
		}
		addressRdo.setBuildingName(getObjectName());
		addressRdo.setBuildingNumber(getNumber());
		addressRdo.setBuildingCorpus(getCorpus());
		addressRdo.setBuildingWing(getWing());
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	/**
	 * @return Номер здания.
	 */
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return Корпус здания.
	 */
	public String getCorpus() {
		return corpus;
	}

	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}

	/**
	 * @return Номер строения.
	 */
	public String getWing() {
		return wing;
	}

	public void setWing(String wing) {
		this.wing = wing;
	}

	/**
	 * @return Почтовый индекс.
	 */
	public String getPostIndex() {
		return postIndex;
	}

	public void setPostIndex(String postIndex) {
		this.postIndex = postIndex;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class BuildingQuery extends LocationQuery<Building> {

		EntityQueryStringFilter<Building> number = createStringFilter(Building_.number);
		EntityQueryStringFilter<Building> corpus = createStringFilter(Building_.corpus);
		EntityQueryStringFilter<Building> wing = createStringFilter(Building_.wing);
		EntityQueryStringFilter<Building> postIndex = createStringFilter(Building_.postIndex);

		public BuildingQuery() {
			super(Building.class);
		}

		public EntityQueryStringFilter<Building> number() {
			return number;
		}

		public EntityQueryStringFilter<Building> corpus() {
			return corpus;
		}

		public EntityQueryStringFilter<Building> wing() {
			return wing;
		}

		public EntityQueryStringFilter<Building> postIndex() {
			return postIndex;
		}

	}

}