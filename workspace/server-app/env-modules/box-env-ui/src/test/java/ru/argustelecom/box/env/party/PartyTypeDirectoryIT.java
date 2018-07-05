package ru.argustelecom.box.env.party;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.UITestUtils.uniqueId;
import static ru.argustelecom.box.env.party.PartyCategory.COMPANY;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class PartyTypeDirectoryIT extends AbstractWebUITest {

    @Test
    public void shouldCreatePartyType(@InitialPage PartyTypeDirectoryPage page) {
        String nameVal = uniqueId("Тестовый тип участника");
        String descriptionVal = "Тестовое описание";

        page.openCreationDialog.click();
        page.partyCategory.select(COMPANY.getName());

        page.creationDialog.name.input(nameVal);
        page.creationDialog.description.input(descriptionVal);
        page.creationDialog.create.click();

        assertEquals(nameVal, page.name.getValue());
        assertEquals(descriptionVal, page.description.getValue());
    }
}