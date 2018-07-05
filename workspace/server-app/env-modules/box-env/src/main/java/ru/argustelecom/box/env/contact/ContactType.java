package ru.argustelecom.box.env.contact;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import ru.argustelecom.box.inf.modelbase.BusinessDirectory;

/**
 * Тип контакта. Справочник.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", uniqueConstraints = {
		@UniqueConstraint(name = "unq_contract_type", columnNames = { "name" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = ContactType.CACHE_REGION_NAME)
public class ContactType extends BusinessDirectory {

	private static final long serialVersionUID = 2360415585974558790L;

	public static final String CACHE_REGION_NAME = "ru.argustelecom.box.env-rw-cache-region";

	@Enumerated(EnumType.STRING)
	@Column(length = 32, nullable = false)
	private ContactCategory category;

	@Column(length = 128, nullable = false)
	private String name;

	@Column(length = 32)
	private String shortName;

	protected ContactType() {
	}

	public ContactType(Long id) {
		super(id);
	}

	public ContactType(ContactCategory category, String name, String shortName) {
		this.category = category;
		this.name = name;
		this.shortName = shortName;
	}

	@Override
	public String getObjectName() {
		return name;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	/**
	 * @return Категория контакта.
	 */
	public ContactCategory getCategory() {
		return category;
	}

	public void setCategory(ContactCategory category) {
		this.category = category;
	}

	/**
	 * @return Наименование типа контакта.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Сокращенное наименование типа контака.
	 */
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}