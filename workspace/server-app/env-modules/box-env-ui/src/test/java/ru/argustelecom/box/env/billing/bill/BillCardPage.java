package ru.argustelecom.box.env.billing.bill;

import org.jboss.arquillian.graphene.page.Location;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/billing/bill/BillCardView.xhtml")
public class BillCardPage extends PageInf {

    @FindByFuzzyId("bill_attributes_form-number_out")
    public OutputText number;

    @FindByFuzzyId("bill_attributes_form-billDate")
    public OutputText billDate;

    @FindByFuzzyId("bill_attributes_form-billAttributes")
    public OutputText billType;

    @FindByFuzzyId("bill_attributes_form-paymentCondition")
    public OutputText paymentCondition;

}