package ru.argustelecom.box.env.report;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/report/ReportTypeEditorView.xhtml")
public class ReportTypeEditorPage extends PageInf {

    @FindByFuzzyId("report_type_tree_form-report_type_create_button")
    private Link openCreationDialog;

    @FindByFuzzyId("report_type_tree_form-report_type_tree")
    public Tree reportTypesTree;

    @FindBy(xpath = "//body")
    public CreationDialog creationDialog;

    @FindBy(xpath = "//body")
    public Attributes attributes;

    class CreationDialog {

        @FindByFuzzyId("report_type_creation_form-report_name")
        public InputText name;

        @FindByFuzzyId("report_type_creation_form-description")
        public InputText description;

        @FindByFuzzyId("report_type_creation_form-create_button")
        public Button create;
    }

    class Attributes {

        @FindByFuzzyId("report_attributes_form-name_out")
        public OutputText name;

        @FindByFuzzyId("report_attributes_form-state")
        public OutputText state;

        @FindByFuzzyId("report_attributes_form-description_out")
        public OutputText description;
    }

    public void openCreationDialog(String requiredItem) {
        openCreationDialog.click();

        driver.findElements(By.cssSelector("#report_type_tree_form-report_type_categories_panel li")).stream()
                .filter(webElement -> webElement.getText().equals(requiredItem))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(""))
                .click();
    }
}