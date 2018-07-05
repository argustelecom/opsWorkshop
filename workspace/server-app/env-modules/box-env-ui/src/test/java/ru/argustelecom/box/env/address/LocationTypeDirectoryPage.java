package ru.argustelecom.box.env.address;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/address/LocationTypeDirectoryView.xhtml")
public class LocationTypeDirectoryPage extends PageInf {

    @FindBy(css = "#location_types_form-managed_buttons a")
    public Link openCreationDialog;

    @FindByFuzzyId("location_type_edit_form-new_name")
    public InputText name;

    @FindByFuzzyId("location_type_edit_form-new_short_name")
    public InputText shortName;

    @FindByFuzzyId("location_type_edit_form-new_level")
    public ComboBox locationLevels;

    @FindByFuzzyId("location_type_edit_form-create_button")
    public Button create;

    @FindByFuzzyId("location_types_form-location_types_table")
    public Table locationTypes;

    @FindByFuzzyId("location_types_form-remove_button")
    private Link delete;

    public void delete() {
        delete.click();
        currentDialog().click("Да");
    }
}
