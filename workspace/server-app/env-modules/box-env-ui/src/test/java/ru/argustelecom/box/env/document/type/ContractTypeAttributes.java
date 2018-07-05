package ru.argustelecom.box.env.document.type;

import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

public class ContractTypeAttributes {

    @FindByFuzzyId("doctype_attributes_form-doctype_name_out")
    public OutputText name;

    @FindByFuzzyId("doctype_attributes_form-provider_out")
    public OutputText provider;

    @FindByFuzzyId("doctype_attributes_form-doctype_customer_type_out")
    public OutputText customerType;

    @FindByFuzzyId("#doctype_attributes_form-doctype_desc_out")
    public OutputText description;
}
