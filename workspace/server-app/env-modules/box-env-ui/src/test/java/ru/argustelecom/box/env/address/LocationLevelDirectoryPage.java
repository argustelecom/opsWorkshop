package ru.argustelecom.box.env.address;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Table;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/address/LocationLevelDirectoryView.xhtml")
public class LocationLevelDirectoryPage extends PageInf {

    @FindBy(css = "#location_levels_form-managed_buttons a")
    public Link openCreationDialog;

    @FindByFuzzyId("location_level_edit_form-new_name")
    public InputText name;

    @FindByFuzzyId("location_level_edit_form-create_button")
    public Button create;

    @FindByFuzzyId("location_levels_form-location_levels_table")
    public Table locationLevels;

}