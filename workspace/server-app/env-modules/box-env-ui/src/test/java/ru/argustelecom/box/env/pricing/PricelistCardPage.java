package ru.argustelecom.box.env.pricing;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.box.env.activity.ActivityFragment;
import ru.argustelecom.box.env.customer.CustomerFragment;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.ArrayList;
import java.util.List;

@Location("views/env/pricing/PricelistCardView.xhtml")
public class PricelistCardPage extends PageInf {

    @FindByFuzzyId("pricelist_attributes_form-name_out")
    public OutputText name;

    @FindByFuzzyId("pricelist_attributes_form-state")
    public OutputText state;

    @FindByFuzzyId("pricelist_attributes_form-valid_from_out")
    public OutputText validFrom;

    @FindByFuzzyId("pricelist_attributes_form-valid_to_out")
    public OutputText validTo;

    @FindBy(css = "#pricelist_segments_form .m-fleft")
    private List<WebElement> segments;

    @FindByFuzzyId("pricelist_products_form-open_dlg_new_product_offering")
    public Link openDialogNewProductOffering;

    @FindByFuzzyId("product_offering_edit_form-new_product_type")
    public ComboBox productTypes;

    @FindByFuzzyId("product_offering_edit_form-provision_terms")
    public ComboBox provisionTerms;

    @FindByFuzzyId("product_offering_edit_form-period_unit")
    public ComboBox periodUnits;

    @FindByFuzzyId("product_offering_edit_form-new_price")
    public InputText priceNewOfferingProduct;

    @FindByFuzzyId("product_offering_edit_form-submit_button")
    public Button saveNewProductOffering;

    @FindByFuzzyId("pricelist_products_form-pricelist_products_table")
    public Table pricelistProductsTable;

    @FindByFuzzyId("pricelist_products_form-pricelist_products_table_paginator_bottom")
    private WebElement productsTablePaginator;

    @FindByFuzzyId("pricelist_attributes_form-tax_rate_out")
    public OutputText taxRateOut;

    @FindBy(xpath = "//body")
    public ActivityFragment activity;

    @FindBy(xpath = "//body")
    public CustomerFragment customerBlock;

    @FindByFuzzyId("pricelist_attributes_form-owners_out")
    public OutputText owner;

    @FindByFuzzyId("product_offering_edit_form-amount")
    public InputText amount;

    public List<String> getSegments() {
        List<String> segmentNames = new ArrayList<>();

        segments.forEach(item -> segmentNames.add(item.getText()));

        return segmentNames;
    }

    public void openLastPaginationPage() {
        WebElement lastPageLink = productsTablePaginator.findElement(By.className("ui-paginator-last"));
        if (lastPageLink.isEnabled()) {
            lastPageLink.click();
        }
    }
}
