package ru.argustelecom.box.env.report;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertTrue;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class ReportTypeMapIT extends AbstractWebUITest {

    @Test
    public void shouldOpenPage(@InitialPage ReportTypeMapPage page) {
        assertTrue(page.getGroups().get(0).equals("Без группы"));
    }
}