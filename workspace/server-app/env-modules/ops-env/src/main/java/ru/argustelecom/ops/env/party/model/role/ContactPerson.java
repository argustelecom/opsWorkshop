package ru.argustelecom.ops.env.party.model.role;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Getter;
import ru.argustelecom.ops.env.party.model.Company;
import ru.argustelecom.ops.env.party.model.PartyRole;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Access(AccessType.FIELD)
public class ContactPerson extends PartyRole {

	@Getter
	@Column(length = 128)
	private String appointment;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	private Company company;

	@Override
	public String getObjectName() {
		return getParty().getObjectName();
	}

	public void changeAppointment(String newAppointment) {
		if (!Objects.equals(this.appointment, newAppointment)) {
			this.appointment = newAppointment;
			// TODO [события предметной области]
			// DomainEvents.instance().fire(new ContactPersonChangedEvent<PersonName>(this, "appointment",
			// oldAppointment, newAppointment));
		}
	}

	protected ContactPerson() {
	}

	@Builder
	protected ContactPerson(Long id, Company company, String appointment) {
		super(id);
		this.company = checkNotNull(company, "Company for ContactPerson is required");
		this.appointment = appointment;
	}

	public static class ContactPersonQuery extends PartyRoleQuery<ContactPerson> {

		private EntityQueryEntityFilter<ContactPerson, Company> company = createEntityFilter(ContactPerson_.company);
		private EntityQueryStringFilter<ContactPerson> appointment = createStringFilter(ContactPerson_.appointment);

		public ContactPersonQuery() {
			super(ContactPerson.class);
		}

		public EntityQueryEntityFilter<ContactPerson, Company> company() {
			return company;
		}

		public EntityQueryStringFilter<ContactPerson> appointment() {
			return appointment;
		}
	}

	private static final long serialVersionUID = -5672910064713028789L;
}
