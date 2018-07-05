package ru.argustelecom.box.env.document.type;

import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

public class BillAttributes {

    @FindByFuzzyId("doctype_attributes_form-doctypedto_name_out")
    public OutputText name;

    @FindByFuzzyId("doctype_attributes_form-doctypedto_customer_type_dto")
    public OutputText customerType;

    @FindByFuzzyId("doctype_attributes_form-doctypedto_period_type")
    public OutputText period;

    @FindByFuzzyId("doctype_attributes_form-doctypedto_payment_sum")
    public OutputText sumToPay;

    @FindByFuzzyId("doctype_attributes_form-doctypedto_grouping")
    public OutputText groupingMethod;

    @FindByFuzzyId("doctype_attributes_form-doctypedto_payment_condition")
    public OutputText paymentCondition;

    @FindByFuzzyId("doctype_attributes_form-doctypedto_desc_out")
    public OutputText description;

    @FindByFuzzyId("doctype_attributes_form-bill_type_attr_provider")
    public OutputText provider;
}