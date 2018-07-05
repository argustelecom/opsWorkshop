package ru.argustelecom.box.env.pricing;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.List;

@Location("views/env/pricing/PricelistJournalView.xhtml")
public class PricelistJournalPage extends PageInf {

    @FindByFuzzyId("pricelist_search_result_form-create_pricelist_button")
    private Button openCreateCommonPricelistDialog;

    @FindByFuzzyId("pricelist_search_result_form-pricelist_creation_modes")
    private ListBox pricelistModes;

    @FindByFuzzyId("pricelist_creation_form-new_name")
    public InputText name;

    @FindByFuzzyId("pricelist_creation_form-new_valid_from_input")
    public Calendar validFrom;

    @FindByFuzzyId("pricelist_creation_form-valid_to_input")
    public Calendar validTo;

    @FindBy(css = "#pricelist_creation_form-customer_segments_panel label")
    private List<WebElement> segments;

    @FindByFuzzyId("pricelist_creation_form-create_button")
    public Button create;

    @FindByFuzzyId("pricelist_creation_form-new_customer_spec_label")
    public ComboBox customerType;

    @FindByFuzzyId("pricelist_creation_form-new_customer_input")
    public AutoComplete customer;

    @FindByFuzzyId("pricelist_creation_form-owners_label")
    public ComboBox owner;

    public void openCreateCommonPricelistDialog(String pricelistCategory) {
        openCreateCommonPricelistDialog.click();
        pricelistModes.select(pricelistCategory);
    }

    public void setSegment(String value) {
        // список сегментов просто так не раскроется, необходимо кликнуть по этому элементу
        driver.findElement(By.cssSelector("#pricelist_creation_form-customer_segments")).click();

        segments.stream()
                .filter(item -> item.getText().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("segments"))
                .click();

        driver.findElement(By.cssSelector("#pricelist_creation_form-customer_segments_panel span.ui-icon-circle-close")).click();
        Graphene.waitGui().until().element(By.cssSelector("#pricelist_creation_form-create_button")).is().visible();
    }
}