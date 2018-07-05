package ru.argustelecom.box.env.party;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.NoSuchElementException;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.party.testdata.CustomerSegmentCreationProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.UITestUtils.*;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class CustomerSegmentDirectoryIT extends AbstractWebUITest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldCreateCustomerSegmentThenDelete(
            @InitialPage CustomerSegmentDirectoryPage page,
            @DataProvider(
                    providerClass = CustomerSegmentCreationProvider.class,
                    contextPropertyName = CustomerSegmentCreationProvider.CUSTOMER_TYPE_NAME
            ) String customerTypeName
    ) {
        String nameVal = uniqueId("Тестовый сегмент");
        String descriptionVal = "Тестовое описание";

        page.openCreationDialog.click();
        page.customerTypes.select(customerTypeName);
        page.name.input(nameVal);
        page.description.input(descriptionVal);
        page.create.click();

        assertEquals(customerTypeName, lastRowCell(page.segmentsTable, 1).getTextString());
        assertEquals(nameVal, lastRowCell(page.segmentsTable, 2).getTextString());
        assertEquals(descriptionVal, lastRowCell(page.segmentsTable, 3).getTextString());

        lastRow(page.segmentsTable).select();
        page.delete();

        exception.expect(NoSuchElementException.class);
        page.segmentsTable.findRow(nameVal);
    }
}