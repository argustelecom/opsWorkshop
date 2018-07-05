package ru.argustelecom.box.env.telephony.tariff;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Table;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/tariff/TelephonyZoneDirectoryView.xhtml")
public class TelephonyZoneDirectoryPage extends PageInf {

    @FindBy(css = "#telephony_zones_form-managed_buttons .fa-edit")
    public Link openCreationDialog;

    @FindByFuzzyId("telephony_zone_creation_form-name")
    public InputText name;

    @FindByFuzzyId("telephony_zone_creation_form-description")
    public InputText description;

    @FindByFuzzyId("telephony_zone_creation_form-save_telephony_zone_button")
    public Button create;

    @FindByFuzzyId("telephony_zones_form-telephony_zones_table")
    public Table telephonyZones;

    @FindByFuzzyId("telephony_zones_form-remove_button")
    private Link delete;

    public void delete() {
        delete.click();
        currentDialog().click("Да");
    }
}