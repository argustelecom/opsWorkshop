package ru.argustelecom.box.env.security;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.UITestUtils.uniqueId;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class RoleListIT extends AbstractWebUITest {

    @Test
    public void shouldCreateRole(@InitialPage RoleListPage roleList, @Page RoleCardPage roleCard) {
        String nameVal = uniqueId("Тестовая роль");
        String descriptionVal = "Тестовое описание";

        roleList.openCreateDialog.click();
        roleList.name.input(nameVal);
        roleList.description.input(descriptionVal);
        roleList.create.click();

        assertEquals(nameVal, roleCard.name.getValue());
        assertEquals(descriptionVal, roleCard.description.getValue());
    }
}
