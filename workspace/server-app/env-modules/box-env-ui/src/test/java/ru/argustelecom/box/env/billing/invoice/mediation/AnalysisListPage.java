package ru.argustelecom.box.env.billing.invoice.mediation;

import org.jboss.arquillian.graphene.page.Location;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/mediation/AnalysisListView.xhtml")
public class AnalysisListPage {

    @FindByFuzzyId("analysis_search_result_form-create_job_button")
    public Button createRechargeJob;

    @FindByFuzzyId("invoice_creation_form-cancel_button")
    public Button cancel;
}