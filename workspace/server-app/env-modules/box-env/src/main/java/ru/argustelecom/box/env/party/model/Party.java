package ru.argustelecom.box.env.party.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Formula;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contact.ContactInfo;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.modelbase.Identifiable;

/**
 * Класс вводящий понятие "Участника" системы, у одного участника может быть несколько {@linkplain PartyRole ролей}.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
public class Party extends BusinessObject implements Printable {

	private static final long serialVersionUID = -1214548598507344900L;

	@OneToMany(mappedBy = "party")
	private List<PartyRole> roles = new ArrayList<>();

	@Embedded
	//@formatter:off
	@AssociationOverride(name = "contacts", joinTable = @JoinTable(name = "party_contacts",
			joinColumns = @JoinColumn(name = "party_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "contact_id", referencedColumnName = "id")))
	//@formatter:on
	private ContactInfo contactInfo = new ContactInfo();

	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.ALL })
	@JoinColumn(name = "type_instance_id", nullable = false)
	private PartyTypeInstance typeInstance;

	@Version
	private Long version;

	//@formatter:off
	@Formula("system.get_party_name(id)")
	//@formatter:on
	@Getter
	@Setter
	private String sortName;

	protected Party() {
	}

	public Party(Long id) {
		super(id);
	}

	/**
	 * Добавляет {@linkplain ru.argustelecom.box.env.party.model.PartyRole роль} для участика.
	 * 
	 * @param role
	 *            добавляемая роль.
	 */
	public void addRole(PartyRole role) {
		roles.add(role);
	}

	@Override
	public PartyRdo createReportData() {
		return new PartyRdo(getId(), getTypeInstance().getPropertyValueMap());
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	/**
	 * @return Список ролей, которые есть у участника (неизменяемый).
	 */
	public List<PartyRole> getRoles() {
		return Collections.unmodifiableList(roles);
	}

	/**
	 * @return Информация о контактах участника.
	 */
	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}

	public PartyTypeInstance getTypeInstance() {
		return typeInstance;
	}

	public void setTypeInstance(PartyTypeInstance typeInstance) {
		this.typeInstance = typeInstance;
	}

	public static class PartyQuery<T extends Identifiable> extends EntityQuery<T> {

		public PartyQuery(Class<T> entityClass) {
			super(entityClass);
		}
	}

}