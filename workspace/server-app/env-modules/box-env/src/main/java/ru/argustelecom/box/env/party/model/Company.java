package ru.argustelecom.box.env.party.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import ru.argustelecom.box.env.party.model.role.ContactPersons;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

/**
 * Участник являющийся организацией.
 */
@Entity
@Access(AccessType.FIELD)
public class Company extends Party {

	private static final long serialVersionUID = 6239804885569782902L;

	@Column(length = 256, nullable = false)
	private String legalName;

	@Column(length = 256)
	private String brandName;

	@Embedded
	//@formatter:off
	@AssociationOverride(name = "persons", joinTable = @JoinTable(name = "company_contact_persons",
			joinColumns = @JoinColumn(name = "company_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "contact_person_id", referencedColumnName = "id")))
	//@formatter:on
	private ContactPersons contactPersons = new ContactPersons();

	protected Company() {
	}

	public Company(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return brandName == null ? legalName : brandName;
	}

	@Override
	public CompanyRdo createReportData() {
		//@formatter:off
		return CompanyRdo.builder()
					.id(getId())
					.properties(getTypeInstance().getPropertyValueMap())
					.legalName(getLegalName())
					.brandName(getBrandName())
				.build();
		//@formatter:on
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	/**
	 * @return Официальное/Юридическое наименование организации.
	 */
	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	/**
	 * @return Наименование торговой марки.
	 */
	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public ContactPersons getContactPersons() {
		return contactPersons;
	}

	public static class CompanyQuery extends PartyQuery<Company> {

		private final EntityQueryStringFilter<Company> legalName;

		public CompanyQuery() {
			super(Company.class);
			legalName = createStringFilter(Company_.legalName);
		}

		public EntityQueryStringFilter<Company> legalName() {
			return legalName;
		}
	}
}