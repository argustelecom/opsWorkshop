package ru.argustelecom.box.env.message;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.*;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class TemplateEditorIT extends AbstractWebUITest {

    @Test
    public void shouldOpenHint(@InitialPage TemplateEditorPage page) {
        page.openHint.click();

        assertTrue(page.hint.isDisplayed());
    }
}