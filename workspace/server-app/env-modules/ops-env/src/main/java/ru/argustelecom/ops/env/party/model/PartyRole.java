package ru.argustelecom.ops.env.party.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.argustelecom.ops.env.party.model.role.Employee;
import ru.argustelecom.ops.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * Класс вводящий понятие роли {@linkplain Party участника в системе}. Один участник может обладать несколькими ролями,
 * например быть {@linkplain Employee работником} и в то же время
 * {@linkplain ru.argustelecom.ops.env.party.model.role.Customer клиентом}. Каждая роль участника будет наделена своим
 * логин/паролем для авторизации в системе.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
public class PartyRole extends BusinessObject {

	private static final long serialVersionUID = 5723085702147240527L;

	@ManyToOne(fetch = FetchType.EAGER)
	private Party party;

	@Version
	private Long version;

	protected PartyRole() {
	}

	public PartyRole(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return party.getObjectName();
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	/**
	 * @return Участник, для которого выделена данная роль.
	 */
	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public static class PartyRoleQuery<T extends PartyRole> extends EntityQuery<T> {
		private EntityQueryEntityFilter<T, Party> party = createEntityFilter(PartyRole_.party);

		public PartyRoleQuery(Class<T> entityClass) {
			super(entityClass);
		}

		public EntityQueryEntityFilter<T, Party> party() {
			return party;
		}
	}

}