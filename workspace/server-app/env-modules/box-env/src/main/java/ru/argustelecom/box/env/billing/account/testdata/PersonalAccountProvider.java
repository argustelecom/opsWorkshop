package ru.argustelecom.box.env.billing.account.testdata;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.box.env.party.testdata.PartyTestDataUtils;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

/**
 * Предоставляет клиента и лицевой счет для теста PersonalAccountIT#shouldCreateTransaction
 * 
 * <li>Тип клиента (CustomerSpec). Ищет или создает тип клиента "Тестовое ФЛ".  
 * <li>Клиент (Customer). Будет создан новый клиент с типом тестового ФЛ.
 * <li>Лицевой счет (PersonalAccount). Будет создан новый лицевой счет.
 * 
 * @author v.semchenko
 */
public class PersonalAccountProvider implements TestDataProvider {

	public static final String CREATED_PERSONAL_ACCOUNT_PROP_NAME = "personal.account.provider.personal.account";
	
	private static final String CUSTOMER_TYPE_KEYWORD = "customer-type-for-personal-account-tests";

	@Inject
	private CustomerTypeTestDataUtils customerTypeTestDataUtils;
	
	@Inject
	private PartyTestDataUtils partyTestDataUtils;
	
	@Inject
	private PersonalAccountTestDataUtils personalAccountTestDataUtils;
	
	
	@Override
	public void provide(TestRunContext testRunContext) {
		Customer customer = partyTestDataUtils.createTestIndividualCustomerByTestCustomerType();
		PersonalAccount personalAccount = personalAccountTestDataUtils.createTestPersonalAccount(customer);

		testRunContext.setBusinessPropertyWithMarshalling(CREATED_PERSONAL_ACCOUNT_PROP_NAME, personalAccount);
	}

}
