package ru.argustelecom.box.env.document.type;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.contract.testdata.AgencyContractTypeCreationProvider;
import ru.argustelecom.box.env.contract.testdata.BilateralContractTypeCreationProvider;
import ru.argustelecom.box.env.contract.testdata.BillTypeCreationProvider;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.*;
import static ru.argustelecom.box.env.UITestUtils.getTreeNode;
import static ru.argustelecom.box.env.UITestUtils.uniqueId;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class DocumentTypeIT extends AbstractWebUITest {

    @Test
    public void shouldCreateBillTypeThenDelete(
            @InitialPage DocumentTypePage page,
            @DataProvider(
                    providerClass = BillTypeCreationProvider.class,
                    contextPropertyName = BillTypeCreationProvider.CUSTOMER_TYPE_NAME
            ) String customerTypeName
    ) {
        String name = uniqueId("Тестовый тип счёта");
        String periodType = PeriodType.CALENDARIAN.toString();
        String periodUnit = PeriodUnit.MONTH.toString();
        String groupingMethod = GroupingMethod.CONTRACT.getName();
        String paymentCondition = PaymentCondition.PREPAYMENT.getName();
        String providerName = getTestRunContextProperty(BillTypeCreationProvider.PROVIDER_NAME, String.class);

        // аналитика захардкожена и добавляется скриптом sql
        String sumToPay = "Сумма начислений за период счета";
        String description = "Тестовое описание";

        page.openCreateDialog("Счёт");
        page.billTypeCreationDialog.name.input(name);
        page.billTypeCreationDialog.customerTypes.select(customerTypeName);
        page.billTypeCreationDialog.setProvider(providerName);
        page.billTypeCreationDialog.periodTypes.select(periodType);
        page.billTypeCreationDialog.periodUnits.select(periodUnit);
        page.billTypeCreationDialog.groupingMethod.select(groupingMethod);
        page.billTypeCreationDialog.paymentCondition.select(paymentCondition);
        page.billTypeCreationDialog.sumToPay.select(sumToPay);
        page.billTypeCreationDialog.description.input(description);
        page.billTypeCreationDialog.create.click();

        assertTrue(getTreeNode(page.documentTypes, "Счёт", name).isPresent());

        assertEquals(name, page.billAttributes.name.getValue());
        assertEquals(customerTypeName, page.billAttributes.customerType.getValue());
        assertEquals(periodType + " / " + periodUnit, page.billAttributes.period.getValue());
        assertEquals(groupingMethod, page.billAttributes.groupingMethod.getValue());
        assertEquals(paymentCondition, page.billAttributes.paymentCondition.getValue());
        assertEquals(sumToPay, page.billAttributes.sumToPay.getValue());
        assertEquals(description, page.billAttributes.description.getValue());
        assertEquals(providerName, page.billAttributes.provider.getValue());

        page.delete();
        assertNull(getTreeNode(page.documentTypes, "Счёт", name));
    }

    @Test
    public void shouldCreateBilateralContractTypeThenDelete(
            @InitialPage DocumentTypePage page,
            @DataProvider(
                    providerClass = BilateralContractTypeCreationProvider.class,
                    contextPropertyName = BilateralContractTypeCreationProvider.OWNER_NAME
            ) String ownerName
    ) {
        String customerTypeName = getTestRunContextProperty(BilateralContractTypeCreationProvider.CUSTOMER_TYPE_NAME, String.class);
        String nameVal = uniqueId("Тестовый тип договора");
        String descriptionVal = "Тестовое описание";

        page.openCreateDialog("Договор");
        page.contractTypeCreationDialog.name.input(nameVal);
        page.contractTypeCreationDialog.description.input(descriptionVal);
        page.contractTypeCreationDialog.providers.select(ownerName);
        page.contractTypeCreationDialog.customerTypes.select(customerTypeName);
        page.contractTypeCreationDialog.create.click();

        assertTrue(getTreeNode(page.documentTypes, "Договор", nameVal).isPresent());

        assertEquals(nameVal, page.contractTypeAttributes.name.getValue());
        assertEquals(descriptionVal, page.contractTypeAttributes.description.getValue());
        assertEquals(customerTypeName, page.contractTypeAttributes.customerType.getValue());
        assertEquals(ownerName, page.contractTypeAttributes.provider.getValue());

        page.delete();
        assertNull(getTreeNode(page.documentTypes, "Договор", nameVal));
    }

    @Test
    public void shouldCreateAgencyContractTypeThenDelete(
            @InitialPage DocumentTypePage page,
            @DataProvider(
                    providerClass = AgencyContractTypeCreationProvider.class,
                    contextPropertyName = AgencyContractTypeCreationProvider.SUPPLIER_NAME
            ) String supplierName
    ) {
        String customerTypeName = getTestRunContextProperty(AgencyContractTypeCreationProvider.CUSTOMER_TYPE_NAME, String.class);
        String nameVal = uniqueId("Тестовый тип договора");
        String descriptionVal = "Тестовое описание";

        page.openCreateDialog("Договор");
        page.contractTypeCreationDialog.name.input(nameVal);
        page.contractTypeCreationDialog.description.input(descriptionVal);
        page.contractTypeCreationDialog.isAgencyContractType.check();
        page.contractTypeCreationDialog.providers.select(supplierName);
        page.contractTypeCreationDialog.customerTypes.select(customerTypeName);
        page.contractTypeCreationDialog.create.click();

        assertTrue(getTreeNode(page.documentTypes, "Договор", nameVal).isPresent());

        assertEquals(nameVal, page.contractTypeAttributes.name.getValue());
        assertEquals(descriptionVal, page.contractTypeAttributes.description.getValue());
        assertEquals(customerTypeName, page.contractTypeAttributes.customerType.getValue());
        assertEquals(supplierName, page.contractTypeAttributes.provider.getValue());

        page.delete();
        assertNull(getTreeNode(page.documentTypes, "Договор", nameVal));
    }
}