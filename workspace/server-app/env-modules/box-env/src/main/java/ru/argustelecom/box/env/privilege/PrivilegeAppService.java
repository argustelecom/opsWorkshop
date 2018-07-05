package ru.argustelecom.box.env.privilege;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.SubscriptionProcessingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.privilege.model.CustomerPrivilege;
import ru.argustelecom.box.env.privilege.model.PersonalAccountPrivilege;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.env.privilege.model.SubscriptionPrivilege;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class PrivilegeAppService implements Serializable {

	private static final long serialVersionUID = -528283685831919595L;

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private SubscriptionProcessingService processingSvc;

	@Inject
	private PrivilegeRepository privilegeRp;

	public SubscriptionPrivilege createTrustPeriodForSubscription(Date validFrom, Date validTo, Long subsId) {
		return privilegeRp.createTrustPeriod(validFrom, validTo, em.find(Subscription.class, subsId));
	}

	public PersonalAccountPrivilege createTrustPeriodForPersonalAccount(Date validFrom, Date validTo, Long accntId) {
		return privilegeRp.createTrustPeriod(validFrom, validTo, em.find(PersonalAccount.class, accntId));
	}

	public CustomerPrivilege createTrustPeriodForCustomer(Date validFrom, Date validTo, Long customerId) {
		return privilegeRp.createTrustPeriod(validFrom, validTo, em.find(Customer.class, customerId));
	}

	public boolean hasSubscriptionPrivilegeInPeriod(Long subscriptionId, Date startDate, Date endDate) {
		return privilegeRp.hasPrivilegeInPeriod(em.find(Subscription.class, subscriptionId), startDate, endDate);
	}

	public boolean hasPersonalAccountPrivilegeInPeriod(Long personalAccountId, Date startDate, Date endDate) {
		return privilegeRp.hasPrivilegeInPeriod(em.find(PersonalAccount.class, personalAccountId), startDate, endDate);
	}

	public boolean hasCustomerPrivilegeInPeriod(Long customerId, Date startDate, Date endDate) {
		return privilegeRp.hasPrivilegeInPeriod(em.find(Customer.class, customerId), startDate, endDate);
	}

	public List<Privilege> findActivePrivilegesBySubscription(Long subscriptionId) {
		return privilegeRp.findActivePrivileges(em.find(Subscription.class, subscriptionId));
	}

	public List<Privilege> findActivePrivilegesByPersonalAccount(Long personalAccountId) {
		return privilegeRp.findActivePrivileges(em.find(PersonalAccount.class, personalAccountId));
	}

	public List<Privilege> findActivePrivilegesByCustomer(Long customerId) {
		return privilegeRp.findActivePrivileges(em.find(Customer.class, customerId));
	}

	public List<Privilege> findPrivilegesBySubscription(Long subscriptionId) {
		return privilegeRp.findPrivileges(em.find(Subscription.class, subscriptionId));
	}

	public List<Privilege> findPrivilegesByPersonalAccount(Long personalAccountId) {
		return privilegeRp.findPrivileges(em.find(PersonalAccount.class, personalAccountId));
	}

	public List<Privilege> findPrivilegesByCustomer(Long customerId) {
		return privilegeRp.findPrivileges(em.find(Customer.class, customerId));
	}

	public void extendPrivilege(Long privilegeId, Date newValidTo) {
		processingSvc.extendPrivilege(em.find(Privilege.class, privilegeId), newValidTo);
	}

	public void closePrivilege(Long privilegeId) {
		processingSvc.closePrivilege(em.find(Privilege.class, privilegeId));
	}

}