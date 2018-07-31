package ru.argustelecom.ops.env.party.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Formula;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.ops.env.contact.ContactInfo;
import ru.argustelecom.ops.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.modelbase.Identifiable;

/**
 * Класс вводящий понятие "Участника" системы, у одного участника может быть несколько {@linkplain PartyRole ролей}.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
public class Party extends BusinessObject {

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
	 * Добавляет {@linkplain ru.argustelecom.ops.env.party.model.PartyRole роль} для участика.
	 * 
	 * @param role
	 *            добавляемая роль.
	 */
	public void addRole(PartyRole role) {
		roles.add(role);
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

	public static class PartyQuery<T extends Identifiable> extends EntityQuery<T> {

		public PartyQuery(Class<T> entityClass) {
			super(entityClass);
		}
	}

}