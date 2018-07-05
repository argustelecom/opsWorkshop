package ru.argustelecom.box.env.login.testdata;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.account.testdata.PersonalAccountTestDataUtils;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

/**
 * Для личного кабинета каждый {@link TestDataProvider} по-сути является расширением {@link PersonalAreaLoginProvider},
 * т.к. единственное, что клиент делает - это логинится в него, таким образом, на момент логина уже должны быть
 * готовы все данные. Провайдер создает n лицевых счетов клиенту, таким образом, позволив в его личном кабинете
 * выбрать один текущий из нескольких
 */
public class PersonalAreaMultiplyPersonalAccountProvider extends PersonalAreaLoginProvider implements TestDataProvider {

    public static final String PERSONAL_ACCOUNTS_PROP_NAME = "personalArea.provider.personalAccounts";

    private static final int PERSONAL_ACCOUNTS_SIZE = 3;

    @Inject
    private PersonalAccountTestDataUtils personalAccountTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {

        // здесь также инициализируем поле customer
        super.provide(testRunContext);

        PersonalAccount[] personalAccounts = new PersonalAccount[PERSONAL_ACCOUNTS_SIZE];

        for (int i = 0; i < PERSONAL_ACCOUNTS_SIZE; i ++) {
            personalAccounts[i] = personalAccountTestDataUtils.createTestPersonalAccount(customer);
        }

        testRunContext.setBusinessPropertyWithMarshalling(PERSONAL_ACCOUNTS_PROP_NAME, personalAccounts);
    }

}
