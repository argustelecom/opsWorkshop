package ru.argustelecom.box.env.address;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class LocationLevelDirectoryIT extends AbstractWebUITest {

    @Test
    public void shouldCreateLocationLevels(@InitialPage LocationLevelDirectoryPage page) {
        String levelName = "Адресный уровень" + UUID.randomUUID().toString();

        page.openCreationDialog.click();
        page.name.input(levelName);
        page.create.click();

        int lastRowIndex = page.locationLevels.getRowCount() - 1;
        assertEquals(levelName, page.locationLevels.getRow(lastRowIndex).getCell(1).getTextString());
    }

}
