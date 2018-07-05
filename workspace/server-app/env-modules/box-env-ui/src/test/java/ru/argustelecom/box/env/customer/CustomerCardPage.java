package ru.argustelecom.box.env.customer;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.box.env.billing.account.PersonalAccountFragment;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/customer/CustomerCardView.xhtml")
public class CustomerCardPage extends PageInf {

    @FindByFuzzyId("customer_attributes_form-type_out")
    private OutputText customerType;

    @FindByFuzzyId("customer_attributes_form-company_type_out")
    private OutputText companyType;

    @FindByFuzzyId("customer_attributes_form-first_name_out")
    private OutputText firstName;

    @FindByFuzzyId("customer_attributes_form-last_name_out")
    private OutputText lastName;

    @FindByFuzzyId("customer_attributes_form-second_name_out")
    private OutputText secondName;

    @FindByFuzzyId("customer_attributes_form-legal_name_out")
    private OutputText legalName;

    @FindByFuzzyId("customer_attributes_form-brand_name_out")
    private OutputText brandName;

    @FindBy(xpath = "//body")
    public PersonalAccountFragment personalAccountBlock;

    public String getCustomerType() {
        return customerType.getValue();
    }

    public String getCompanyType() {
        return companyType.getValue();
    }

    public String getFirstName() {
        return firstName.getValue();
    }

    public String getLastName() {
        return lastName.getValue();
    }

    public String getSecondName() {
        return secondName.getValue();
    }

    public String getLegalName() {
        return legalName.getValue();
    }

    public String getBrandName() {
        return brandName.getValue();
    }

}
