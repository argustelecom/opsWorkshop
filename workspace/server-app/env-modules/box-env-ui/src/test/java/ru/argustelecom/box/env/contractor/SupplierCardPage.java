package ru.argustelecom.box.env.contractor;

import org.jboss.arquillian.graphene.page.Location;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/contractor/SupplierCardView.xhtml")
public class SupplierCardPage extends PageInf {

    @FindByFuzzyId("customer_attributes_form-legal_name_out")
    public OutputText legalName;

    @FindByFuzzyId("customer_attributes_form-brand_name_out")
    public OutputText brandName;

    @FindByFuzzyId("customer_attributes_form-party_type")
    public OutputText partyType;

}
