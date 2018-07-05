package ru.argustelecom.box.env.customer;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.testdata.CustomerCreationProvider;
import ru.argustelecom.system.inf.testframework.it.ui.navigation.LocationParameterProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomerCardIT extends AbstractWebUITest implements LocationParameterProvider {

    @Override
    public Map<String, String> provideLocationParameters() {
        Map<String, String> params = new HashMap<>();

        Customer customer = getTestRunContextProperty(CustomerCreationProvider.CUSTOMER_PROP_NAME, Customer.class);
        params.put("customer", new EntityConverter().convertToString(customer));

        return params;
    }

    /**
     * Сценарий id = C100179 Создание лицевого счета
     * <p>
     * Предварительные условия: Открыта карточка клиента, которому требуется создать лицевой счет.
     * <p>
     * Сценарий:
     * <ol>
     * <li>В блоке "Лицевые счета" нажать кнопку "Добавить лицевой счет".
     * <li>В диалоге выбрать: Номер
     * <li>Нажать кнопку "Создать".
     * <li>Проверить, что в блоке "Лицевые счета" появилась плитка с созданным лицевым счете и его номер совпадает с
     * введённым. Баланс лицевого счета равен нулю.
     * <p>
     * Исполнитель: [v.sysoev]
     */
    @Test
    //@formatter:off
    public void shouldCreatePersonalAccount(
            @InitialPage CustomerCardPage customerCard,
            @DataProvider(
                    contextPropertyName = CustomerCreationProvider.CUSTOMER_PROP_NAME,
                    providerClass = CustomerCreationProvider.class
            ) Customer customer
    ) {
        //@formatter:on
        String number = "ЛС " + UUID.randomUUID().toString();

        customerCard.personalAccountBlock.openCreateDialog();
        customerCard.personalAccountBlock.setNumber(number);
        customerCard.personalAccountBlock.create();

        assertTrue(customerCard.personalAccountBlock.getPersonalAccounts().contains(number));
        assertEquals("0.00", customerCard.personalAccountBlock.getBalance(number));
    }
}
