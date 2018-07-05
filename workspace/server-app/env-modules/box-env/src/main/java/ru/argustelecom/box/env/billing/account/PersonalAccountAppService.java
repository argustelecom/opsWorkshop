package ru.argustelecom.box.env.billing.account;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.service.ApplicationService;

import static com.google.common.base.Preconditions.checkNotNull;

@ApplicationService
public class PersonalAccountAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private PersonalAccountBalanceService personalAccountBalanceSvc;

	public List<PersonalAccount> findPersonalAccount(Long customerId, String number) {
		List<PersonalAccount> personalAccounts = em.find(Customer.class, customerId).getPersonalAccounts();
		return number != null
				? personalAccounts.stream().filter(personalAccount -> personalAccount.getNumber().contains(number))
						.collect(Collectors.toList())
				: personalAccounts;
	}

	public List<PersonalAccount> findPersonalAccounts(Long customerId) {
		return em.find(Customer.class, customerId).getPersonalAccounts();
	}

	public Money getAvailableBalance(Long accountId) {
		checkNotNull(accountId);

		PersonalAccount account = em.find(PersonalAccount.class, accountId);
		checkNotNull(account);

		return personalAccountBalanceSvc.getAvailableBalance(account);
	}

	private static final long serialVersionUID = 1980708551661035395L;
}
