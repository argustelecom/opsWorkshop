package ru.argustelecom.box.env.customer;

import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

public class CustomerFragment {

    @FindByFuzzyId("customer_info_form-organization_type")
    public OutputText organizationType;

    @FindByFuzzyId("customer_info_form-last_name")
    public OutputText lastName;

    @FindByFuzzyId("customer_info_form-first_name")
    public OutputText firstName;

    @FindByFuzzyId("customer_info_form-second_name")
    public OutputText secondName;

}
