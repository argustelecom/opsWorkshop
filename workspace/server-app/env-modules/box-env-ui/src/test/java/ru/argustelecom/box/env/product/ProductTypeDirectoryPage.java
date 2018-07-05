package ru.argustelecom.box.env.product;

import org.jboss.arquillian.graphene.page.Location;

import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/product/ProductTypeDirectoryView.xhtml")
public class ProductTypeDirectoryPage extends PageInf {

    @FindByFuzzyId("product_type_tree_form-create_product_type_button")
    private Link addProductButton;

    @FindByFuzzyId("product_type_creation_form-new_name")
    public InputText productName;

    @FindByFuzzyId("product_type_creation_form-new_description")
    public InputText productDesc;

    @FindByFuzzyId("product_type_creation_form-create_button")
    public Button createProductButton;

    @FindByFuzzyId("product_type_attributes_form-product_type_name_out")
    public OutputText name;

    @FindByFuzzyId("product_type_attributes_form-product_type_group_out")
    public OutputText groupName;

    @FindByFuzzyId("product_type_attributes_form-product_type_category_out")
    public OutputText categoryName;

    @FindByFuzzyId("product_type_tree_form-product_type_categories")
    private ListBox menuitems;

    public void openCreationDialog(String value) {
        addProductButton.click();
        menuitems.select(value);
    }
}