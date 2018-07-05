package ru.argustelecom.box.env.billing.invoice.mediation;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class ChargingListIT extends AbstractWebUITest {

    @Test
    public void shouldOpenPage(@InitialPage ChargingListPage page) {
        page.createRechargeJob.click();
        page.cancel.click();
    }
}