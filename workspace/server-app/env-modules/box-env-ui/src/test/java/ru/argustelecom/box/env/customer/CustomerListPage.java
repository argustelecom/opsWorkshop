package ru.argustelecom.box.env.customer;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.box.env.filter.FilterFragment;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ListBox;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/customer/CustomerListView.xhtml")
public class CustomerListPage extends PageInf {

    @FindByFuzzyId("customer_search_result_form-create_customer_button")
    public Button openCreateDialog;

    @FindByFuzzyId("customer_search_result_form-possible_customer_categories")
    private ListBox customerCategories;

    @FindByFuzzyId("customer_creation_form-new_type_label")
    public ComboBox customerTypes;

    @FindByFuzzyId("customer_creation_form-new_first_name")
    public InputText firstName;

    @FindByFuzzyId("customer_creation_form-new_last_name")
    public InputText lastName;

    @FindByFuzzyId("customer_creation_form-new_second_name")
    public InputText secondName;

    @FindByFuzzyId("customer_creation_form-create_button")
    public Button create;

    @FindByFuzzyId("customer_creation_form-new_legal_name")
    public InputText legalName;

    @FindByFuzzyId("customer_creation_form-new_brand_name")
    public InputText brandName;

    @FindBy(xpath = "//body")
    public FilterFragment filterBlock;

    public void openCreateDialog(String value) {
        openCreateDialog.click();
        customerCategories.select(value);
    }
}
