package ru.argustelecom.box.env.party.model.role;

import static ru.argustelecom.box.env.billing.account.model.PersonalAccountState.ACTIVE;
import static ru.argustelecom.box.env.contact.ContactCategory.EMAIL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.fasterxml.jackson.databind.JsonNode;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount_;
import ru.argustelecom.box.env.contact.Contact;
import ru.argustelecom.box.env.contact.ContactInfo_;
import ru.argustelecom.box.env.contact.ContactType;
import ru.argustelecom.box.env.contact.Contact_;
import ru.argustelecom.box.env.contact.CustomContact;
import ru.argustelecom.box.env.contact.EmailContact;
import ru.argustelecom.box.env.contact.PhoneContact;
import ru.argustelecom.box.env.contact.SkypeContact;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.Contract_;
import ru.argustelecom.box.env.party.model.Company;
import ru.argustelecom.box.env.party.model.CompanyRdo;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.CustomerTypeInstance;
import ru.argustelecom.box.env.party.model.CustomerTypeInstance_;
import ru.argustelecom.box.env.party.model.Party;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.PartyTypeInstance;
import ru.argustelecom.box.env.party.model.PartyTypeInstance_;
import ru.argustelecom.box.env.party.model.Party_;
import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.env.party.model.PersonRdo;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.env.type.model.TypeInstance.EntityQueryPropertiesFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * Роль описывающая {@linkplain ru.argustelecom.box.env.party.model.Party участника} как клиента.
 */
@Entity
@Access(AccessType.FIELD)
public class Customer extends PartyRole implements Printable {

	private static final long serialVersionUID = -8817800186801461924L;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.ALL })
	@JoinColumn(name = "type_instance_id", nullable = false)
	private CustomerTypeInstance typeInstance;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_id")
	private List<PersonalAccount> personalAccounts = new ArrayList<>();

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

	@Override
	public CustomerRdo createReportData() {
		PersonRdo personRdo = getParty() instanceof Person ? (PersonRdo) getParty().createReportData() : null;
		CompanyRdo companyRdo = getParty() instanceof Company ? (CompanyRdo) getParty().createReportData() : null;

		//@formatter:off
		return CustomerRdo.builder()
					.id(getId())
					.person(personRdo)
					.company(companyRdo)
					.vip(isVip())
					.properties(getTypeInstance().getPropertyValueMap())
				.build();
		//@formatter:on
	}

	public List<PersonalAccount> getActivePersonalAccounts() {
		return personalAccounts.stream().filter(account -> account.getState().equals(ACTIVE))
				.collect(Collectors.toList());
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

	public CustomerTypeInstance getTypeInstance() {
		return typeInstance;
	}

	public void setTypeInstance(CustomerTypeInstance typeInstance) {
		this.typeInstance = typeInstance;
	}

	public List<PersonalAccount> getPersonalAccounts() {
		return personalAccounts;
	}

	public void setPersonnelAccounts(List<PersonalAccount> personnelAccounts) {
		this.personalAccounts = personnelAccounts;
	}

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

		private Join<T, CustomerTypeInstance> customerTypeInstanceJoin;
		private Join<Customer, Contact> contactJoin;
		private Join<Customer, Party> partyJoin;
		private Join<Party, PartyTypeInstance> partyTypeInstanceJoin;
		private Join<Customer, PhoneContact> telephoneNumberContactJoin;
		private Join<Customer, EmailContact> emailAddressContactJoin;
		private Join<Customer, SkypeContact> skypeLoginContactJoin;
		private Join<Customer, CustomContact> customContactJoin;
		private Join<Customer, PersonalAccount> personalAccountJoin;

		private EntityQueryEntityFilter<T, CustomerType> customerType;
		private EntityQueryPropertiesFilter<T> customerProperties;
		private EntityQueryPropertiesFilter<T> partyProperties;

		public CustomerQuery(Class<T> entityClass) {
			super(entityClass);
			initCustomerType();
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

		public Predicate byContract(String value) {
			return root().in(contractSubquery(value));
		}

		public Predicate byPersonalAccount(String value) {
			return criteriaBuilder().equal(personalAccountJoin().get(PersonalAccount_.number),
					createParam(PersonalAccount_.number, value));
		}

		public EntityQueryEntityFilter<T, CustomerType> customerType() {
			return customerType;
		}

		public EntityQueryPropertiesFilter<T> partyProperties() {
			if (partyProperties == null) {
				Path<JsonNode> propsPath = partyTypeInstanceJoin().get(PartyTypeInstance_.props);
				partyProperties = new EntityQueryPropertiesFilter<>(this, propsPath, PartyTypeInstance_.props);
			}
			return partyProperties;
		}

		public EntityQueryPropertiesFilter<T> customerProperties() {
			if (customerProperties == null) {
				Path<JsonNode> propsPath = customerTypeInstanceJoin.get(CustomerTypeInstance_.props);
				customerProperties = new EntityQueryPropertiesFilter<>(this, propsPath, CustomerTypeInstance_.props);
			}
			return customerProperties;
		}

		@SuppressWarnings("unchecked")
		private void initCustomerType() {
			customerTypeInstanceJoin = root().join(Customer_.typeInstance, JoinType.INNER);
			Path<CustomerType> customerTypePath = customerTypeInstanceJoin.get(CustomerTypeInstance_.type);
			customerType = createEntityFilter(customerTypePath, CustomerTypeInstance_.type);
		}

		private Join<Customer, PhoneContact> phoneContactJoin() {
			if (telephoneNumberContactJoin == null) {
				telephoneNumberContactJoin = typedContactJoin();
			}
			return telephoneNumberContactJoin;
		}

		private Join<Customer, EmailContact> emailContactJoin() {
			if (emailAddressContactJoin == null) {
				emailAddressContactJoin = typedContactJoin();
			}
			return emailAddressContactJoin;
		}

		private Join<Customer, SkypeContact> skypeContactJoin() {
			if (skypeLoginContactJoin == null) {
				skypeLoginContactJoin = typedContactJoin();
			}
			return skypeLoginContactJoin;
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

		private Join<Party, PartyTypeInstance> partyTypeInstanceJoin() {
			if (partyTypeInstanceJoin == null) {
				partyTypeInstanceJoin = partyJoin().join(Party_.typeInstance);
			}
			return partyTypeInstanceJoin;
		}

		private Join<Customer, PersonalAccount> personalAccountJoin() {
			if (personalAccountJoin == null) {
				personalAccountJoin = root().join(Customer_.personalAccounts.getName());
			}
			return personalAccountJoin;
		}

		private Subquery<Customer> contractSubquery(String value) {
			Subquery<Customer> subquery = criteriaQuery().subquery(Customer.class);
			Root<Contract> root = subquery.from(Contract.class);
			subquery.select(root.get(Contract_.customer));
			subquery.where(criteriaBuilder().equal(root.get(Contract_.documentNumber), value));
			return subquery;
		}

		private <CT extends Contact> Join<Customer, CT> typedContactJoin() {
			return root().join(Customer_.party).join(Party_.contactInfo).join(ContactInfo_.contacts.getName());
		}

	}

}