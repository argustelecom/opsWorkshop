package ru.argustelecom.box.env.party;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/party/PartyTypeDirectoryView.xhtml")
public class PartyTypeDirectoryPage {

    @FindBy(css = "#party_type_tree_form-party_type_tree_managed_buttons i.fa-edit")
    public Link openCreationDialog;

    @FindByFuzzyId("party_type_tree_form-possible_party_type_categories")
    public ListBox partyCategory;

    @FindBy(xpath = "//body")
    public CreationDialog creationDialog;

    @FindByFuzzyId("party_type_attributes_form-name_out")
    public OutputText name;

    @FindByFuzzyId("party_type_attributes_form-description_out")
    public OutputText description;

    class CreationDialog {

        @FindByFuzzyId("party_type_creation_form-new_name")
        public InputText name;

        @FindByFuzzyId("party_type_creation_form-new_description")
        public InputText description;

        @FindByFuzzyId("party_type_creation_form-create_button")
        public Button create;
    }
}