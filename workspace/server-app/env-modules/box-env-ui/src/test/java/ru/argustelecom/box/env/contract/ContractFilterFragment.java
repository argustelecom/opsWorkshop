package ru.argustelecom.box.env.contract;

import ru.argustelecom.box.env.filter.FilterFragment;
import ru.argustelecom.system.inf.testframework.it.ui.comp.AutoComplete;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

public class ContractFilterFragment extends FilterFragment {

    @FindByFuzzyId("contract_search_form-number")
    private InputText number;

    @FindByFuzzyId("contract_search_form-customer_spec_label")
    private ComboBox customerType;

    @FindByFuzzyId("contract_search_form-customer_input")
    private AutoComplete customerName;

    public void setNumber(String value) {
        number.input(value);
    }

    public void setCustomerType(String value) {
        customerType.select(value);
    }

    public void setCustomerName(String value) {
        customerName.search(value);
    }
}