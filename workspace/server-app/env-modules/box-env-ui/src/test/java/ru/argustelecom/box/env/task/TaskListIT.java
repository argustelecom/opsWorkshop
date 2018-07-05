package ru.argustelecom.box.env.task;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class TaskListIT extends AbstractWebUITest {

    @Test
    public void shouldOpenPage(@InitialPage TaskListPage page) {
        page.filterFragment.clickFilterButton();
    }

}