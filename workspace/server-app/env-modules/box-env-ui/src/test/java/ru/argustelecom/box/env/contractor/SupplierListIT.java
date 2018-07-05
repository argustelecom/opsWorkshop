package ru.argustelecom.box.env.contractor;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.party.testdata.PartyTypeProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertEquals;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class SupplierListIT extends AbstractWebUITest {

    /**
     * Создание поставщика
     */
    @Test
    public void shouldCreateSupplier(
            @InitialPage SupplierListPage supplierList,
            @Page SupplierCardPage supplierCard,
            @DataProvider(
                    providerClass = PartyTypeProvider.class,
                    contextPropertyName = PartyTypeProvider.PARTY_TYPE_NAME
            ) String partyType
    ) {
        String legalName = "Тестовый поставщик";
        String brandName = "Тестовый бренд";

        supplierList.openCreationDialog.click();
        supplierList.legalName.input(legalName);
        supplierList.brandName.input(brandName);
        supplierList.partyTypes.select(partyType);
        supplierList.create.click();

        assertEquals(legalName, supplierCard.legalName.getValue());
        assertEquals(brandName, supplierCard.brandName.getValue());
        assertEquals(partyType, supplierCard.partyType.getValue());
    }
}