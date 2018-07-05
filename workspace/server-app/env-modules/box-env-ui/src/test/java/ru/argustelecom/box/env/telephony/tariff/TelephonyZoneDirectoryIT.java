package ru.argustelecom.box.env.telephony.tariff;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.NoSuchElementException;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Row;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.UITestUtils.uniqueId;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class TelephonyZoneDirectoryIT extends AbstractWebUITest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldCreateTelephonyZoneThenDelete(@InitialPage TelephonyZoneDirectoryPage page) {
        String nameVal = uniqueId("Тестовая зона");
        String descriptionVal = "Тестовое описание";

        page.openCreationDialog.click();

        page.name.input(nameVal);
        page.description.input(descriptionVal);
        page.create.click();

        Row createdZoneRow = page.telephonyZones.findRow(nameVal);
        assertEquals(nameVal, createdZoneRow.getCell(1).getTextString());
        assertEquals(descriptionVal, createdZoneRow.getCell(2).getTextString());

        createdZoneRow.select();
        page.delete();

        exception.expect(NoSuchElementException.class);
        page.telephonyZones.findRow(nameVal);
    }
}
