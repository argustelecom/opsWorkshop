package ru.argustelecom.box.env.address;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/address/AddressDirectoryView.xhtml")
public class AddressDirectoryPage extends PageInf {

    @FindByFuzzyId("location_tree_form-create_location_button")
    public Link openCreationDialog;

    @FindByFuzzyId("location_tree_form-location_categories")
    public ListBox categories;

    @FindBy(xpath = "//body")
    public CreationDialog creationDialog;

    class CreationDialog {

        @FindByFuzzyId("location_creation_form-new_name")
        public InputText name;

        @FindByFuzzyId("location_creation_form-create_button")
        public Button create;

        @FindByFuzzyId("location_creation_form-new_level_label")
        public ComboBox levels;

        @FindByFuzzyId("location_creation_form-new_type_label")
        public ComboBox types;
    }

    @FindBy(xpath = "//body")
    public Attributes attributes;

    class Attributes {

        @FindByFuzzyId("location_attributes_form-name_out")
        public OutputText name;

        @FindByFuzzyId("location_attributes_form-type_out")
        public OutputText type;
    }

    @FindBy(css = "#location_tree_form-tree_tab_view ul.ui-tree-container")
    public Tree locationTree;

    @FindBy(css = "#building_form-managed_buttons a:nth-child(1)")
    public Link openBuildingCreationDialog;

    @FindBy(css = "#building_form-managed_buttons a:nth-child(2)")
    private Link deleteBuilding;

    @FindBy(xpath = "//body")
    public BuildingCreationDialog buildingCreationDialog;

    class BuildingCreationDialog {

        @FindByFuzzyId("building_edit_form-new_number")
        public InputText number;

        @FindByFuzzyId("building_edit_form-new_corpus")
        public InputText corpus;

        @FindByFuzzyId("building_edit_form-new_wing")
        public InputText wing;

        @FindByFuzzyId("building_edit_form-new_post_index")
        public InputText postIndex;

        @FindByFuzzyId("building_edit_form-new_landmark")
        public InputText landmark;

        @FindByFuzzyId("building_edit_form-submit_building_button")
        public Button create;
    }

    @FindByFuzzyId("building_form-building_table")
    public Table buildings;

    public void deleteBuilding() {
        deleteBuilding.click();
        currentDialog().click("Да");
    }
}