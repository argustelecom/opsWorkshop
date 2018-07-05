package ru.argustelecom.box.env.commodity;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.ArrayList;
import java.util.List;

public class Options extends PageInf {

    @FindBy(css = "#service_type_option_type_form-property_managed_buttons i")
    public Link openAddOptionTypeDialog;

    @FindBy(css = "#edit_service_type_to_option_type_form-edit_service_type_to_option_type_dlg " +
            "div.ui-selectmanymenu td:nth-child(2)")
    private List<WebElement> optionsTypes;

    public void select(String value) {
        optionsTypes.stream()
                .filter(webElement -> webElement.getText().equals(value))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("optionsTypes"))
                .click();
    }

    @FindByFuzzyId("edit_service_type_to_option_type_form-create_button")
    public Button add;

    @FindBy(css = "#service_type_option_type_form-option_type_list li")
    private List<WebElement> addedOptionTypes;

    public List<String> getOptionTypeNames() {
        List<String> optionTypeNames = new ArrayList<>();
        addedOptionTypes.forEach(webElement -> optionTypeNames.add(webElement.getText()));
        return optionTypeNames;
    }
}