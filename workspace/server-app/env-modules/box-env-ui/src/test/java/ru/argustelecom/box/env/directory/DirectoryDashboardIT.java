package ru.argustelecom.box.env.directory;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static junit.framework.TestCase.assertTrue;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class DirectoryDashboardIT extends AbstractWebUITest {

    @Test
    public void shouldOpenPage(@InitialPage DirectoryDashboardPage page) {
        assertTrue(page.getBreadcrumbsNames().contains("Карта справочников"));
    }
}