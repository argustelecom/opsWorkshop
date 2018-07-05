package ru.argustelecom.box.env.filter;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Dialog;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.ArrayList;
import java.util.List;

public class FilterFragment extends PageInf {

    @FindByFuzzyId("apply_filter_button")
    private Button filtersButton;

    @FindByFuzzyId("save_as")
    private WebElement saveAs;

    @FindByFuzzyId("new_filter_preset")
    private InputText name;

    @FindByFuzzyId("list_filter_preset_creation_form-create_button")
    private Button create;

    @FindBy(css = "[id$=apply_filter_list_content] li a:first-child")
    private List<WebElement> presets;

    @FindByFuzzyId("current-filter")
    private OutputText currentPreset;

    @FindByFuzzyId("find")
    private Button find;

    public void clickFilterButton() {
        filtersButton.click();
    }

    public void saveAs(String value) {
        saveAs.click();
        name.input(value);
        create.click();
    }

    public List<String> getPresets() {
        List<String> presetsNames = new ArrayList<>();

        presets.forEach(item -> presetsNames.add(item.getText()));

        return presetsNames;
    }

    public void select(String value) {
        presets.stream()
                .filter(item -> item.getText().equals(value))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("presets"))
                .click();
    }

    public String getCurrentPreset() {
        return currentPreset.getValue();
    }

    public void find() {
        find.click();
    }

    public void delete(String value) {
        presets.stream()
                .filter(item -> item.getText().equals(value))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("presets"))
                .findElement(By.xpath("//*/parent::*"))
                .findElement(By.cssSelector("i.fa-trash"))
                .click();

        currentDialog().click("Да");
    }
}