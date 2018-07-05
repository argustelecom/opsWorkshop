package ru.argustelecom.box.env.billing.bill;

import org.jboss.arquillian.graphene.page.Location;

import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.AutoComplete;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Calendar;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/billing/bill/BillListView.xhtml")
public class BillListPage extends PageInf {

    @FindByFuzzyId("bill_search_result_form-create_bill_button")
    public Button openCreateBillDialog;

    @FindByFuzzyId("bill_search_result_form-create_bill_panel-create_one_bill_button")
    public Link createOneBill;

    @FindByFuzzyId("bill_create_form-bill_type_label")
    public ComboBox billTypes;

    @FindByFuzzyId("bill_create_form-bill_number")
    public InputText number;

    @FindByFuzzyId("bill_create_form-customer_type_label")
    public ComboBox customerTypes;

    @FindByFuzzyId("bill_create_form-customer_name_input")
    public AutoComplete customer;

    @FindByFuzzyId("bill_create_form-grouping_method_label")
    public ComboBox groupingMethods;

    @FindByFuzzyId("bill_create_form-payment_condition_label")
    public ComboBox paymentConditions;

    @FindByFuzzyId("bill_create_form-period-start_date_input")
    public Calendar startDate;

    @FindByFuzzyId("bill_create_form-period-end_date_input")
    public Calendar endDate;

    @FindByFuzzyId("bill_create_form-bill_date_input")
    public Calendar billDate;

    @FindByFuzzyId("bill_create_form-bill_template_label")
    public ComboBox templates;

    @FindByFuzzyId("bill_create_form-bill_create_button")
    public Button createBill;

    @FindByFuzzyId("bill_create_form-bill_cancel_button")
    public Button cancelCreation;

}
