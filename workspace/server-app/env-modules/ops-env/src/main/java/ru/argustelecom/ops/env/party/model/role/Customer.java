package ru.argustelecom.ops.env.party.model.role;

import static ru.argustelecom.ops.env.contact.ContactCategory.EMAIL;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.ops.env.contact.Contact;
import ru.argustelecom.ops.env.contact.ContactInfo_;
import ru.argustelecom.ops.env.contact.ContactType;
import ru.argustelecom.ops.env.contact.Contact_;
import ru.argustelecom.ops.env.contact.CustomContact;
import ru.argustelecom.ops.env.contact.EmailContact;
import ru.argustelecom.ops.env.party.model.Party;
import ru.argustelecom.ops.env.party.model.PartyRole;
import ru.argustelecom.ops.env.party.model.Party_;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;

/**
 * Роль описывающая {@linkplain ru.argustelecom.ops.env.party.model.Party участника} как клиента.
 */
@Entity
@Access(AccessType.FIELD)
public class Customer extends PartyRole {

	private static final long serialVersionUID = -8817800186801461924L;

	@Column(nullable = false)
	private boolean vip;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_email_id")
	private EmailContact mainEmail;

	protected Customer() {
	}

	public Customer(Long id) {
		super(id);
	}

	public EmailContact getCorrespondenceEmail() {
		return mainEmail != null ? mainEmail : findFirstEmail();
	}

	private EmailContact findFirstEmail() {
		return (EmailContact) getParty().getContactInfo().getContacts().stream()
				.filter(contact -> contact.getType().getCategory().equals(EMAIL)).findFirst().orElse(null);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************
	public boolean isVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	public EmailContact getMainEmail() {
		return mainEmail;
	}

	public void setMainEmail(EmailContact mainEmail) {
		this.mainEmail = mainEmail;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	@SuppressWarnings("rawtypes")
	public static class CustomerQuery<T extends Customer> extends EntityQuery<T> {

		private Join<Customer, Contact> contactJoin;
		private Join<Customer, Party> partyJoin;
		private Join<Customer, EmailContact> emailAddressContactJoin;
		private Join<Customer, CustomContact> customContactJoin;

		public CustomerQuery(Class<T> entityClass) {
			super(entityClass);
		}

		public Predicate byContactType(ContactType type) {
			return criteriaBuilder().equal(contactJoin().get(Contact_.type), createParam(Contact_.type, type));
		}

		public Predicate byContact(ContactType type, String value) {
			//@formatter:off
			return criteriaBuilder().isTrue(criteriaBuilder().function("system.party_has_contact", Boolean.class,
					partyJoin().get(Party_.id),
					criteriaBuilder().literal(type.getId()),
					criteriaBuilder().literal(value))
			);
			//@formatter:on
		}

		private Join<Customer, EmailContact> emailContactJoin() {
			if (emailAddressContactJoin == null) {
				emailAddressContactJoin = typedContactJoin();
			}
			return emailAddressContactJoin;
		}

		private Join<Customer, CustomContact> customContactJoin() {
			if (customContactJoin == null) {
				customContactJoin = typedContactJoin();
			}
			return customContactJoin;
		}

		private Join<Customer, Contact> contactJoin() {
			if (contactJoin == null) {
				contactJoin = typedContactJoin();
			}
			return contactJoin;
		}

		private Join<Customer, Party> partyJoin() {
			if (partyJoin == null) {
				partyJoin = root().join(Customer_.party.getName());
			}
			return partyJoin;
		}

		private <CT extends Contact> Join<Customer, CT> typedContactJoin() {
			return root().join(Customer_.party).join(Party_.contactInfo).join(ContactInfo_.contacts.getName());
		}

	}

}