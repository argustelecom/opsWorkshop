package ru.argustelecom.box.env.billing.invoice.chargejob;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/billing/invoice/chargejob/ChargeJobListView.xhtml")
public class ChargeJobListPage {

    @FindByFuzzyId("charge_job_search_result_form-recharge_button")
    public Button createRechargeJob;

    @FindBy(css = "span.ui-icon-closethick")
    public Link closeWizard;

}