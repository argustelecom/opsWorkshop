package ru.argustelecom.box.pa;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Ignore;
import org.junit.Test;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.login.testdata.PersonalAreaMultiplyPersonalAccountProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertEquals;

/**
 * @author kostd
 */
@LoginProvider(providerClass = PersonalAreaMultiplyPersonalAccountProvider.class)
public class PersonalAreaIT extends AbstractWebUITest {

    /**
     * Сценарий id = C112081 Смена текущего лицевого счета
     * <p>
     * Предварительные условия: В системе для клиента был создан личный кабинет.
     * FIXME должен предоставлять провайдер У клиента создано несколько лицевых счетов.
     * <p>
     * Сценарий:
     * <ol>
     * <li>В блоке "Лицевые счета" нажать на плитку лицевого счета, который требуется просмотреть.
     * <li>Проверить, что в блоке "Лицевой счёт" отображен выбранный счёт.
     * <p>
     * Исполнитель: [v.sysoev]
     */
    @Test
    public void shouldChangeCurrentPersonalAccount(@InitialPage PersonalAreaPage page) {

        PersonalAccount[] personalAccounts =
                getTestRunContextProperty(PersonalAreaMultiplyPersonalAccountProvider.PERSONAL_ACCOUNTS_PROP_NAME, PersonalAccount[].class);

        // по умолчания в личном кабинете отображается первый созданный лицевой счёт, поэтому берём последний, т.к. он
        // точно не текущий и тест имеет смысл
        String account = personalAccounts[personalAccounts.length - 1].getBeautifulNumber();

        page.setCurrentAccount(account);

        assertEquals(account, page.getCurrentAccount());
    }

}