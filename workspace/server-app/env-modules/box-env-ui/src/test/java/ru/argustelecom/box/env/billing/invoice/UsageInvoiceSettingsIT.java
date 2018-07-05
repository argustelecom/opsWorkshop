package ru.argustelecom.box.env.billing.invoice;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class UsageInvoiceSettingsIT extends AbstractWebUITest {

    @Test
    public void shouldOpenPage(@InitialPage UsageInvoiceSettingsPage page) {
        String previousValue;

        page.scheduleSettings.edit();
        previousValue = page.scheduleUnitAmount.getValue();
        page.scheduleUnitAmount.input("2");
        page.scheduleSettings.save();

        page.scheduleSettings.edit();
        page.scheduleUnitAmount.input(previousValue);
        page.scheduleSettings.save();
    }
}
