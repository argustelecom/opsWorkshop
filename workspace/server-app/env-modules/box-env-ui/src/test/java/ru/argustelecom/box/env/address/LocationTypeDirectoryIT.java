package ru.argustelecom.box.env.address;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.NoSuchElementException;
import ru.argustelecom.box.env.address.testdata.LocationLevelProvider;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Row;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.UITestUtils.uniqueId;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class LocationTypeDirectoryIT extends AbstractWebUITest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldCreateRegionLocationTypeThenDelete(
            @InitialPage LocationTypeDirectoryPage page,
            @DataProvider(
                    contextPropertyName = LocationLevelProvider.LEVEL_NAME,
                    providerClass = LocationLevelProvider.class
            ) String locationLevelName
    ) {
        String nameVal = uniqueId("Тестовый тип");
        String shortNameVal = "тт";

        page.openCreationDialog.click();
        page.name.input(nameVal);
        page.shortName.input(shortNameVal);
        page.locationLevels.select(locationLevelName);
        page.create.click();

        Row createdLocationTypeRow = page.locationTypes.findRow(nameVal);

        assertEquals(nameVal, createdLocationTypeRow.getCell(2).getTextString());
        assertEquals(shortNameVal, createdLocationTypeRow.getCell(3).getTextString());

        createdLocationTypeRow.select();
        page.delete();

        exception.expect(NoSuchElementException.class);
        page.locationTypes.findRow(nameVal);
    }
}