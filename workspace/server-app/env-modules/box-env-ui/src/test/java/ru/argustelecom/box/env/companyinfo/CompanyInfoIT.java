package ru.argustelecom.box.env.companyinfo;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.party.testdata.NonPrincipalOwnerProvider;
import ru.argustelecom.box.env.party.testdata.PartyTypeProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.*;
import static ru.argustelecom.box.env.UITestUtils.*;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class CompanyInfoIT extends AbstractWebUITest {

    @Test
    public void shouldMakePrincipal(
            @InitialPage CompanyInfoPage page,
            @DataProvider(
                    providerClass = NonPrincipalOwnerProvider.class,
                    contextPropertyName = NonPrincipalOwnerProvider.NON_PRINCIPAL_OWNER_NAME
            ) String nonPrincipalName
    ) {
        // Почему-то страница открывается промотанной вниз, приходится промотать вверх, чтобы найти кнопку makePrincipal
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, -document.body.scrollHeight);");

        getTreeNode(page.owners, nonPrincipalName).select();
        assertFalse(page.makePrincipal.isDisabled());

        page.makePrincipal.click();
        assertTrue(page.makePrincipal.isDisabled());
    }

    @Test
    public void shouldCreateOwnerThenCreateAdditionalParam(
            @InitialPage CompanyInfoPage page,
            @DataProvider(
                    providerClass = PartyTypeProvider.class,
                    contextPropertyName = PartyTypeProvider.PARTY_TYPE_NAME
            ) String partyTypeName
    ) {
        // Почему-то страница открывается промотанной вниз, приходится промотать вверх, чтобы найти кнопку openCreationDialog
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, -document.body.scrollHeight);");

        String nameVal = uniqueId("Тестовый владелец");
        String taxRateVal = "10";

        page.openCreationDialog.click();
        page.creationDialog.name.input(nameVal);
        page.creationDialog.partyTypes.select(partyTypeName);
        page.creationDialog.taxRate.clear().input(taxRateVal);
        page.creationDialog.create.click();

        assertEquals(nameVal, page.attributes.name.getValue());
        assertEquals(partyTypeName, page.attributes.partyType.getValue());
        assertEquals(taxRateVal + " %", page.attributes.taxRate.getValue());

        //FIXME [BOX-3121]
       /* nameVal = uniqueId("Тестовое свойство");
        String keywordVal = uniqueId();
        String valueVal = "Тестовое значение";

        page.openAdditionalParamCreationDialog.click();
        page.additionalParamCreationDialog.name.input(nameVal);
        page.additionalParamCreationDialog.keyword.input(keywordVal);
        page.additionalParamCreationDialog.value.input(valueVal);
        page.additionalParamCreationDialog.create.click();

        assertEquals(nameVal, lastRow(page.additionalParams).getCell(0).getTextString());
        assertEquals(keywordVal, lastRow(page.additionalParams).getCell(1).getTextString());
        assertEquals(valueVal, lastRow(page.additionalParams).getCell(2).getTextString());*/
    }
}