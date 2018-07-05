package ru.argustelecom.box.env.privilege;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.privilege.model.PrivilegeType.TRUST_PERIOD;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.chrono.DateUtils.before;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.google.common.collect.Ordering;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.privilege.model.CustomerPrivilege;
import ru.argustelecom.box.env.privilege.model.CustomerPrivilege.CustomerPrivilegeQuery;
import ru.argustelecom.box.env.privilege.model.PersonalAccountPrivilege;
import ru.argustelecom.box.env.privilege.model.PersonalAccountPrivilege.PersonalAccountPrivilegeQuery;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.env.privilege.model.PrivilegeType;
import ru.argustelecom.box.env.privilege.model.SubscriptionPrivilege;
import ru.argustelecom.box.env.privilege.model.SubscriptionPrivilege.SubscriptionPrivilegeQuery;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Repository
public class PrivilegeRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequenceService;

	public SubscriptionPrivilege createTrustPeriod(Date validFrom, Date validTo, Subscription subs) {
		return createPrivilege(validFrom, validTo, subs, TRUST_PERIOD);
	}

	public PersonalAccountPrivilege createTrustPeriod(Date validFrom, Date validTo, PersonalAccount account) {
		checkArgument(account != null);
		checkArgument(validFrom != null && validTo != null && validTo.after(validFrom));
		checkState(!hasPrivilegeInPeriod(account, validFrom, validTo));

		PersonalAccountPrivilege instance = new PersonalAccountPrivilege(genId(), validFrom, validTo, account);
		em.persist(instance);
		em.flush();
		return instance;
	}

	public CustomerPrivilege createTrustPeriod(Date validFrom, Date validTo, Customer customer) {
		checkArgument(customer != null);
		checkArgument(validFrom != null && validTo != null && validTo.after(validFrom));
		checkState(!hasPrivilegeInPeriod(customer, validFrom, validTo));

		CustomerPrivilege instance = new CustomerPrivilege(genId(), validFrom, validTo, customer);
		em.persist(instance);
		em.flush();
		return instance;
	}

	public SubscriptionPrivilege createPrivilege(Date validFrom, Date validTo, Subscription subs, PrivilegeType type) {
		checkArgument(subs != null);
		checkArgument(!subs.inState(SubscriptionState.CLOSED));
		checkArgument(validFrom != null && validTo != null && validTo.after(validFrom));
		checkState(!hasPrivilegeInPeriod(subs, validFrom, validTo));

		SubscriptionPrivilege instance = new SubscriptionPrivilege(genId(), type, validFrom, validTo, subs);
		em.persist(instance);
		em.flush();
		return instance;
	}

	public List<Privilege> findActivePrivileges(Subscription subscription) {
		List<Privilege> privileges = new ArrayList<>();
		privileges.addAll(findActiveSubscriptionPrivileges(subscription));
		privileges.addAll(findActivePrivileges(subscription.getPersonalAccount()));
		return privileges;
	}

	public List<Privilege> findActivePrivileges(PersonalAccount personalAccount) {
		List<Privilege> privileges = new ArrayList<>();
		privileges.addAll(findActivePersonalAccountPrivileges(personalAccount));
		privileges.addAll(findActivePrivileges(personalAccount.getCustomer()));
		return privileges;
	}

	public List<Privilege> findActivePrivileges(Customer customer) {
		List<Privilege> privileges = new ArrayList<>();
		privileges.addAll(findActiveCustomerPrivileges(customer));
		return privileges;
	}

	public List<Privilege> findPrivileges(Subscription subscription) {
		List<Privilege> privileges = new ArrayList<>();

		SubscriptionPrivilegeQuery query = new SubscriptionPrivilegeQuery();
		query.and(query.subscription().equal(subscription));
		privileges.addAll(query.createTypedQuery(em).getResultList());

		privileges.addAll(findPrivileges(subscription.getPersonalAccount()));
		return privileges.stream().collect(Collectors.toList());
	}

	public List<Privilege> findPrivileges(PersonalAccount personalAccount) {
		List<Privilege> privileges = new ArrayList<>();

		PersonalAccountPrivilegeQuery query = new PersonalAccountPrivilegeQuery();
		query.and(query.personalAccount().equal(personalAccount));
		privileges.addAll(query.createTypedQuery(em).getResultList());

		privileges.addAll(findPrivileges(personalAccount.getCustomer()));
		return privileges;
	}

	public List<Privilege> findPrivileges(Customer customer) {
		List<Privilege> privileges = new ArrayList<>();
		CustomerPrivilegeQuery query = new CustomerPrivilegeQuery();
		query.and(query.customer().equal(customer));
		privileges.addAll(query.createTypedQuery(em).getResultList());
		return privileges;
	}

	public Map<Subscription, List<Privilege>> findPrivileges(List<Subscription> subscriptions, Date start, Date end) {

		checkRequiredArgument(subscriptions, "subscriptions");
		checkRequiredArgument(start, "startDate");
		checkRequiredArgument(end, "endDate");
		checkArgument(before(start, end));

		if (subscriptions.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Subscription, List<Privilege>> result = findPrivilegesBySubs(subscriptions, start, end);
		subscriptions.forEach(s -> result.putIfAbsent(s, new ArrayList<>()));

		List<PersonalAccount> accounts = subscriptions.stream().map(Subscription::getPersonalAccount).collect(toList());
		Map<PersonalAccount, List<Privilege>> searchByAccountsResult = findPrivilegesByAccounts(accounts, start, end);
		result.keySet().forEach(subs -> {
			if (searchByAccountsResult.containsKey(subs.getPersonalAccount())) {
				result.get(subs).addAll(searchByAccountsResult.get(subs.getPersonalAccount()));
			}
		});

		List<Customer> customers = accounts.stream().map(PersonalAccount::getCustomer).collect(toList());
		Map<Customer, List<Privilege>> searchByCustomerResult = findPrivilegesByCustomers(customers, start, end);
		result.keySet().forEach(subs -> {
			if (searchByCustomerResult.containsKey(subs.getPersonalAccount().getCustomer())) {
				result.get(subs).addAll(searchByCustomerResult.get(subs.getPersonalAccount().getCustomer()));
			}
		});

		result.forEach((subs, privileges) -> privileges.sort(nullsLast(comparing(Privilege::getValidTo))));
		return result;

	}

	private static final String FIND_ALL_PRIVILEGES_BY_SUBSCRIPTIONS_INS_PERIOD = "PrivilegeRepository.findAllPrivilegesBySubscriptionsInsPeriod";

	// Запрос написан верно. Да, d.startDate < :endDate и да d.endDate > :startDate. Таким образом получится пересечение
	// привилегий с указанным периодом [:startDate .. :endDate]
	//@formatter:off
	@NamedQuery(name = FIND_ALL_PRIVILEGES_BY_SUBSCRIPTIONS_INS_PERIOD, query
			= "select p "
			+ "	  from SubscriptionPrivilege p "
			+ "  where p.subscription in :subscriptions "
			+ "    and p.validFrom < :endDate "
			+ "    and p.validTo > :startDate "
	)
	//@formatter:on
	private Map<Subscription, List<Privilege>> findPrivilegesBySubs(List<Subscription> subscriptions, Date start,
			Date end) {

		if (subscriptions.isEmpty()) {
			return Collections.emptyMap();
		}

		TypedQuery<SubscriptionPrivilege> query = em.createNamedQuery(FIND_ALL_PRIVILEGES_BY_SUBSCRIPTIONS_INS_PERIOD,
				SubscriptionPrivilege.class);
		query.setParameter("subscriptions", subscriptions);
		query.setParameter("startDate", start);
		query.setParameter("endDate", end);

		return query.getResultList().stream().collect(groupingBy(p -> ((SubscriptionPrivilege) p).getSubscription()));
	}

	private static final String FIND_ALL_PRIVILEGES_BY_PERSONAL_ACCOUNT_INS_PERIOD = "PrivilegeRepository.findAllPrivilegesByPersonalAccountInsPeriod";

	// Запрос написан верно. Да, d.startDate < :endDate и да d.endDate > :startDate. Таким образом получится пересечение
	// привилегий с указанным периодом [:startDate .. :endDate]
	//@formatter:off
	@NamedQuery(name = FIND_ALL_PRIVILEGES_BY_PERSONAL_ACCOUNT_INS_PERIOD, query
			= "select p "
			+ "	  from PersonalAccountPrivilege p "
			+ "  where p.personalAccount in :accounts "
			+ "    and p.validFrom < :endDate "
			+ "    and p.validTo > :startDate "
	)
	//@formatter:on
	private Map<PersonalAccount, List<Privilege>> findPrivilegesByAccounts(List<PersonalAccount> accounts,
			Date startDate, Date endDate) {

		if (accounts.isEmpty()) {
			return Collections.emptyMap();
		}

		TypedQuery<PersonalAccountPrivilege> query = em
				.createNamedQuery(FIND_ALL_PRIVILEGES_BY_PERSONAL_ACCOUNT_INS_PERIOD, PersonalAccountPrivilege.class);
		query.setParameter("accounts", accounts);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		return query.getResultList().stream()
				.collect(groupingBy(p -> ((PersonalAccountPrivilege) p).getPersonalAccount()));
	}

	private static final String FIND_ALL_PRIVILEGES_BY_CUSTOMER_INS_PERIOD = "PrivilegeRepository.findAllPrivilegesByCustomerInsPeriod";

	// Запрос написан верно. Да, d.startDate < :endDate и да d.endDate > :startDate. Таким образом получится пересечение
	// привилегий с указанным периодом [:startDate .. :endDate]
	//@formatter:off
	@NamedQuery(name = FIND_ALL_PRIVILEGES_BY_CUSTOMER_INS_PERIOD, query
			= "select c "
			+ "	  from CustomerPrivilege c "
			+ "  where c.customer in :customers "
			+ "    and c.validFrom < :endDate "
			+ "    and c.validTo > :startDate "
	)
	//@formatter:on
	private Map<Customer, List<Privilege>> findPrivilegesByCustomers(List<Customer> customers, Date startDate,
			Date endDate) {

		if (customers.isEmpty()) {
			return Collections.emptyMap();
		}

		TypedQuery<CustomerPrivilege> query = em.createNamedQuery(FIND_ALL_PRIVILEGES_BY_CUSTOMER_INS_PERIOD,
				CustomerPrivilege.class);
		query.setParameter("customers", customers);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		return query.getResultList().stream().collect(groupingBy(p -> ((CustomerPrivilege) p).getCustomer()));
	}

	/**
	 * Ищет действующую на указанную дату привилегию для подписки. Приоритеты привилегий: Привилегия подписки/Привилегия
	 * ЛС/Привилегия Клиента
	 *
	 * @return Действующая привилегия для Подписки/ЛС/Клиента или <b>null</b> если такой нет.
	 */
	public Privilege findAvailablePrivilege(Subscription subscription, Date date) {
		return findActivePrivileges(subscription).stream().filter(privilege -> privilege.isActive(date))
				.sorted(new ByPrivilegeClassComparator()).findFirst().orElse(null);
	}

	public boolean hasPrivilegeInPeriod(Subscription subscription, Date startDate, Date endDate) {
		return hasPrivilegeInPeriod(findActiveSubscriptionPrivileges(subscription), startDate, endDate);
	}

	public boolean hasPrivilegeInPeriod(PersonalAccount personalAccount, Date startDate, Date endDate) {
		return hasPrivilegeInPeriod(findActivePersonalAccountPrivileges(personalAccount), startDate, endDate);
	}

	public boolean hasPrivilegeInPeriod(Customer customer, Date startDate, Date endDate) {
		return hasPrivilegeInPeriod(findActiveCustomerPrivileges(customer), startDate, endDate);
	}

	private Long genId() {
		return idSequenceService.nextValue(Privilege.class);
	}

	private List<SubscriptionPrivilege> findActiveSubscriptionPrivileges(Subscription subscription) {
		SubscriptionPrivilegeQuery query = new SubscriptionPrivilegeQuery();
		query.and(query.subscription().equal(subscription)).and(query.validTo().greaterThen(new Date()));
		return query.createTypedQuery(em).getResultList();
	}

	private List<PersonalAccountPrivilege> findActivePersonalAccountPrivileges(PersonalAccount personalAccount) {
		PersonalAccountPrivilegeQuery query = new PersonalAccountPrivilegeQuery();
		query.and(query.personalAccount().equal(personalAccount)).and(query.validTo().greaterThen(new Date()));
		return query.createTypedQuery(em).getResultList();
	}

	private List<CustomerPrivilege> findActiveCustomerPrivileges(Customer customer) {
		CustomerPrivilegeQuery query = new CustomerPrivilegeQuery();
		query.and(query.customer().equal(customer)).and(query.validTo().greaterThen(new Date()));
		return query.createTypedQuery(em).getResultList();
	}

	private boolean hasPrivilegeInPeriod(Collection<? extends Privilege> privileges, Date startDate, Date endDate) {
		return privileges.stream().anyMatch(
				privilege -> !privilege.getValidFrom().after(endDate) && !privilege.getValidTo().before(startDate));
	}

	private static final long serialVersionUID = 2681234348929433592L;

	private static class ByPrivilegeClassComparator implements Comparator<Privilege> {

		private static final Ordering<String> BY_CLASS_NAME = Ordering.explicit(
				SubscriptionPrivilege.class.getSimpleName(), PersonalAccountPrivilege.class.getSimpleName(),
				CustomerPrivilege.class.getSimpleName());

		@Override
		public int compare(Privilege o1, Privilege o2) {
			return BY_CLASS_NAME.compare(o1.getClass().getSimpleName(), o2.getClass().getSimpleName());
		}

	}
}