package ru.argustelecom.box.env.login.testdata;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.party.CustomerCategory.PERSON;

import java.util.UUID;

import javax.inject.Inject;

import ru.argustelecom.box.env.login.PersonalAreaLoginRepository;
import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.PartyCategory;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.box.env.party.testdata.PartyTestDataUtils;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

/**
 * Обеспечивает возможность входа для тестов личного кабинета
 * <p>
 * Создает учетную запись пользователя ЛК и все необходимое: (пока только) Customer
 * <p>
 * Примечание: если пишешь тест на ui личного кабинета, сразу подключай этот провайдер. Потому что дефолтной УЗ для
 * входа нет, тест не сможет залогиниться
 * <p>
 * 
 * @author kostd
 *
 */
public class PersonalAreaLoginProvider implements TestDataProvider {

	@Inject
	private PersonalAreaLoginRepository loginRepository;

	@Inject
	private CustomerTypeTestDataUtils customerTypeTestDataUtils;

	@Inject
	private PartyTestDataUtils partyTestDataUtils;

	protected Customer customer;

	/*
	 * CustomerSpec, затем по ней Customer и уже наконец PALogin
	 * 
	 */
	@Override
	public void provide(TestRunContext testRunContext) {

		CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();

		checkState(customerType != null);
		customer = partyTestDataUtils.createTestIndividualCustomer(customerType);

		String pass = UUID.randomUUID().toString().substring(0, 16);

		// пока для простоты в имя логина запихнем прям пароль(это облегчает воспроизведение ошибки теста вручную). Кто
		// знает, тот воспользуется.
		// #TODO: может быть потенциальным отверстием в безопасности, если этот код когданить сработает на продуктивах.
		String userName = "ZZ_PA-AUTOTESTS-" + pass;

		loginRepository.createLogin(customer, userName, pass);

		testRunContext.setBusinessPropertyWithMarshalling(TestDataProvider.CREATED_LOGIN_PROP_NAME, userName);
		testRunContext.setBusinessPropertyWithMarshalling(TestDataProvider.CREATED_PASS_PROP_NAME, pass);

	}
}
