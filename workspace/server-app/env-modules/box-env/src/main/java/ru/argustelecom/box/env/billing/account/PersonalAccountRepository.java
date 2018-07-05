package ru.argustelecom.box.env.billing.account;

import static ru.argustelecom.box.env.billing.account.model.PersonalAccountState.ACTIVE;

import java.io.Serializable;
import java.util.Currency;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.account.model.Reserve;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.numerationpattern.NumberGenerator;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Repository
public class PersonalAccountRepository implements Serializable {

	private static final long serialVersionUID = 731428953373061L;

	public static final String NEXT_PERSONAL_ACCOUNT_NUMBER = "PersonalAccountRepository.nextPersonalAccountNumber";
	private static final String FIND_ALL = "PersonalAccountRepository.findAll";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService iss;

	@Inject
	private NumberGenerator numberGenerator;

	public PersonalAccount createPersonalAccount(@NotNull Customer customer, String number,
			@NotNull Currency currency) {
		PersonalAccount account = new PersonalAccount(iss.nextValue(PersonalAccount.class));
		account.setCustomer(customer);
		account.setNumber(number == null ? numberGenerator.generateNumber(PersonalAccount.class) : number);
		account.setCurrency(currency);
		account.setState(ACTIVE);
		account.setThreshold(Money.ZERO);

		em.persist(account);

		customer.getPersonalAccounts().add(account);

		return account;
	}

	public Reserve createReserve(PersonalAccount account, @NotNull Money amount) {
		Reserve reserve = new Reserve(iss.nextValue(Reserve.class), amount, account);
		em.persist(reserve);
		return reserve;
	}

	public void removeReserve(Reserve reserve) {
		em.remove(reserve);
	}

	@NamedNativeQuery(name = NEXT_PERSONAL_ACCOUNT_NUMBER, query = "select nextval('system.gen_personal_account_number')")
	public String nextContractNumber() {
		return em.createNamedQuery(NEXT_PERSONAL_ACCOUNT_NUMBER).getSingleResult().toString();
	}

	@NamedQuery(name = FIND_ALL, query = "from PersonalAccount")
	public List<PersonalAccount> findAll() {
		return em.createNamedQuery(FIND_ALL, PersonalAccount.class).getResultList();
	}

}
