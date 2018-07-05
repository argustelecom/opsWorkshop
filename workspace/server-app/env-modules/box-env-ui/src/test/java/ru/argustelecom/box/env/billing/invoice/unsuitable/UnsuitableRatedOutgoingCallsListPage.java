package ru.argustelecom.box.env.billing.invoice.unsuitable;

import org.jboss.arquillian.graphene.page.Location;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/billing/invoice/unsuitable/UnsuitableRatedOutgoingCallsListView.xhtml")
public class UnsuitableRatedOutgoingCallsListPage {

    @FindByFuzzyId("unsuitable_rated_outgoing_calls_search_result_form-repeat_button")
    public Button createRechargeJob;

    @FindByFuzzyId("unsuitable_rated_outgoing_calls_repeat_processing_form-processing_cancel_button")
    public Button cancel;

}