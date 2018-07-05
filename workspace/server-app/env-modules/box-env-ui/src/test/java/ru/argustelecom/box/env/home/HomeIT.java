package ru.argustelecom.box.env.home;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertTrue;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class HomeIT extends AbstractWebUITest {

    @Test
    public void shouldOpenPage(@InitialPage HomePage page) {
        assertTrue(page.greeting.isDisplayed());
    }

}