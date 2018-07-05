package ru.argustelecom.box.env.queue;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.assertTrue;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class QueueManagerIT extends AbstractWebUITest {

    @Test
    public void shouldOpenPage(@InitialPage QueueManagerPage page) {
        assertTrue(page.start.isDisplayed());
    }
}