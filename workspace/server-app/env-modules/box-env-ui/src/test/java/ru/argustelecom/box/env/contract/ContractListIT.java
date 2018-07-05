package ru.argustelecom.box.env.contract;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.contract.testdata.ContractCreationProvider;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.ParamsDonator;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import static org.junit.Assert.assertEquals;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class ContractListIT extends AbstractWebUITest {

    public static class ContractListITDonator implements ParamsDonator {

        @Override
        public void donate(TestRunContext testRunContext, String methodName) {

            ContractCategory contractCategory = ContractCategory.BILATERAL;
            switch (methodName) {
                case "shouldCreateBilateralContract": {
                    break;
                }
                case "shouldCreateAgencyContract": {
                    contractCategory = ContractCategory.AGENCY;
                    break;
                }
            }
            testRunContext.setProviderParam(ContractCreationProvider.DESIRED_CONTRACT_CATEGORY, contractCategory);
        }
    }

    /**
     * Сценарий id = 129198 Создание договора
     * <p>
     * Сценарий:
     * <ol>
     * <li>В блоке "Поиск" нажать кнопку "Создать договор".
     * <li>В диалоге ввести: "Тип"
     * <li>В диалоге ввести: "Клиент"
     * <li>В диалоге ввести: "Действует с"
     * <li>В диалоге ввести: "По"
     * <li>В диалоге ввести: "Номер"
     * <li>Нажать кнопку "Создать".
     * <li>Проверить, что: Открыта страница созданного договора
     * <li>Проверить, что: Поле "Тип" заполнено в соответствии с указанными при создании данными
     * <li>Проверить, что: Поле "Клиент" заполнено в соответствии с указанными при создании данными
     * <li>Проверить, что: Поле "Действует с" заполнено в соответствии с указанными при создании данными
     * <li>Проверить, что: Поле "По" заполнено в соответствии с указанными при создании данными
     * <li>Проверить, что: Поле "Номер" заполнено в соответствии с указанными при создании данными
     * <p>
     * Исполнитель: [v.sysoev]
     */
    @Test
    //@formatter:off
    public void shouldCreateBilateralContract(
            @InitialPage ContractListPage contractList,
            @Page ContractCardPage contractCard,
            @DataProvider(
                    providerClass = ContractCreationProvider.class,
                    contextPropertyName = ContractCreationProvider.CONTRACT_TYPE_NAME,
                    donatorClass = ContractListITDonator.class
            ) String contractType
    ) {
        //@formatter:on
        String customer = getTestRunContextProperty(ContractCreationProvider.CUSTOMER_LAST_NAME, String.class);
        String validFrom = "14.11.2017";
        String validTo = "15.12.2018";
        String number = "100879-8789 AB";

        contractList.openDialogCreateContract.click();

        contractList.type.select(contractType);
        contractList.customer.search(customer).selectSingleResult();
        contractList.validFrom.setValue(validFrom);
        contractList.validTo.setValue(validTo);
        contractList.number.input(number);
        contractList.paymentConditions.select(PaymentCondition.PREPAYMENT.getName());
        contractList.create.click();

        assertEquals("Номер", number, contractCard.number.getValue());
        assertEquals("Тип", contractType, contractCard.type.getValue());
        assertEquals("Клиент", customer, contractCard.customerBlock.lastName.getValue());
        assertEquals("Действует с", validFrom, contractCard.validFrom.getValue());
        assertEquals("По", validTo, contractCard.validTo.getValue());
    }

    /**
     * Создание трехстороннего договора
     */
    @Test
    public void shouldCreateAgencyContract(
            @InitialPage ContractListPage contractList,
            @Page ContractCardPage contractCard,
            @DataProvider(
                    providerClass = ContractCreationProvider.class,
                    contextPropertyName = ContractCreationProvider.CONTRACT_TYPE_NAME,
                    donatorClass = ContractListITDonator.class
            ) String contractType
    ) {
        String customer = getTestRunContextProperty(ContractCreationProvider.CUSTOMER_LAST_NAME, String.class);
        String validFrom = "14.11.2017";
        String validTo = "15.12.2018";
        String number = "100879-8789 AB";
        String broker = getTestRunContextProperty(ContractCreationProvider.BROKER_NAME, String.class);

        contractList.openDialogCreateContract.click();

        contractList.type.select(contractType);
        contractList.customer.search(customer).selectSingleResult();
        contractList.validFrom.setValue(validFrom);
        contractList.validTo.setValue(validTo);
        contractList.number.input(number);
        contractList.paymentConditions.select(PaymentCondition.PREPAYMENT.getName());
        contractList.broker.select(broker);
        contractList.create.click();

        assertEquals("Номер", number, contractCard.number.getValue());
        assertEquals("Тип", contractType, contractCard.type.getValue());
        assertEquals("Клиент", customer, contractCard.customerBlock.lastName.getValue());
        assertEquals("Действует с", validFrom, contractCard.validFrom.getValue());
        assertEquals("По", validTo, contractCard.validTo.getValue());
        assertEquals("Агент", broker, contractCard.broker.getValue());
    }
}