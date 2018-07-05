package ru.argustelecom.box.env.billing.account.testdata;

import java.io.Serializable;
import java.util.Currency;
import java.util.UUID;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.account.PersonalAccountRepository;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.party.model.role.Customer;

public class PersonalAccountTestDataUtils implements Serializable{
	
	private static final long serialVersionUID = 6607839642421652628L;
	
	@Inject
	private PersonalAccountRepository personalAccountRepository;

	/**
	 * Создание лицевого счета
	 * 
	 * @param customer
	 * @return
	 */
	public PersonalAccount createTestPersonalAccount(Customer customer) {
		Currency currency = Currency.getInstance("RUB");

		PersonalAccount personalAccount = personalAccountRepository.createPersonalAccount(
				customer, UUID.randomUUID().toString().substring(0, 10), currency
		);

		return personalAccount;
	}

}