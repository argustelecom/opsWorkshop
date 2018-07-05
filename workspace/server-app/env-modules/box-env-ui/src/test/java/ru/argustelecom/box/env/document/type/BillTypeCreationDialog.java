package ru.argustelecom.box.env.document.type;

import org.jboss.arquillian.graphene.Graphene;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.List;

public class BillTypeCreationDialog extends PageInf {

    @FindByFuzzyId("bill_type_create_form-bill_type_name")
    public InputText name;

    @FindByFuzzyId("bill_type_create_form-bill_type_customer_type")
    public ComboBox customerTypes;

    @FindByFuzzyId("bill_type_create_form-bill_type_period_type")
    public ComboBox periodTypes;

    @FindBy(css = "dd #bill_type_create_form-bill_type_period_unit")
    public ComboBox periodUnits;

    @FindByFuzzyId("bill_type_create_form-bill_type_grouping")
    public ComboBox groupingMethod;

    @FindByFuzzyId("bill_type_create_form-bill_type_payment_condition")
    public ComboBox paymentCondition;

    @FindBy(css = "dd #bill_type_create_form-bill_type_summary_bill_anal")
    public ComboBox sumToPay;

    @FindByFuzzyId("bill_type_create_form-bill_type_desc")
    public InputText description;

    @FindByFuzzyId("bill_type_create_form-create_button")
    public Button create;

    @FindBy(css = "#bill_type_create_form-bill_type_providers_panel ul li")
    private List<WebElement> providers;

    public void setProvider(String value) {
        driver.findElement(By.cssSelector("#bill_type_create_form-bill_type_providers")).click();

        providers.stream()
                .filter(item -> item.getText().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("providers"))
                .click();

        driver.findElement(By.cssSelector("#bill_type_create_form-bill_type_providers_panel span.ui-icon-circle-close"))
                .click();
        Graphene.waitGui().until().element(By.cssSelector("#bill_type_create_form-create_button")).is().visible();
    }
}
