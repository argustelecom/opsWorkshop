package ru.argustelecom.box.env.commodity;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.ArrayList;
import java.util.List;

public class TelephonyZones {

    @FindBy(css = "#telephony_option_type_zones_form-property_managed_buttons i")
    public Link openAddTelephonyZoneDialog;

    @FindBy(css = "#edit_telephony_option_type_zones_form-edit_telephony_option_type_zones_dlg td:nth-child(2)")
    private List<WebElement> telephonyZones;

    public void select(String value) {
        telephonyZones.stream()
                .filter(webElement -> webElement.getText().equals(value))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("telephonyZones"))
                .click();
    }

    @FindByFuzzyId("edit_telephony_option_type_zones_form-create_button")
    public Button add;

    @FindBy(css = "   #telephony_option_type_zones_form div.ui-datalist li")
    private List<WebElement> addedTelephonyZones;

    public List<String> getTelephonyZonesNames() {
        List<String> telephonyZonesNames = new ArrayList<>();
        addedTelephonyZones.forEach(webElement -> telephonyZonesNames.add(webElement.getText()));
        return telephonyZonesNames;
    }
}