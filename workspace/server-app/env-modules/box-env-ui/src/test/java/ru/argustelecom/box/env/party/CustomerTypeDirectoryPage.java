package ru.argustelecom.box.env.party;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/party/CustomerTypeDirectoryView.xhtml")
public class CustomerTypeDirectoryPage {

    @FindBy(css = "#customer_type_tree_form-customer_type_tree_managed_buttons i.fa-edit")
    public Link openCreationDialog;

    @FindByFuzzyId("customer_type_tree_form-possible_customer_type_categories")
    public ListBox customerCategory;

    @FindBy(xpath = "//body")
    public CreationDialog creationDialog;

    @FindByFuzzyId("customer_type_attributes_form-name_out")
    public OutputText name;

    @FindByFuzzyId("customer_type_attributes_form-new_party_type_out")
    public OutputText partyType;

    @FindByFuzzyId("customer_type_attributes_form-description_out")
    public OutputText description;

    class CreationDialog {

        @FindByFuzzyId("customer_type_creation_form-new_name")
        public InputText name;

        @FindByFuzzyId("customer_type_creation_form-new_description")
        public InputText description;

        @FindByFuzzyId("customer_type_creation_form-create_button")
        public Button create;

        @FindByFuzzyId("customer_type_creation_form-new_party_type_label")
        public ComboBox partyType;
    }
}