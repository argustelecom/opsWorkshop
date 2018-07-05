package ru.argustelecom.box.env.document.type;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Tree;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.List;

@Location("views/env/document/type/DocumentTypeDirectoryView.xhtml")
public class DocumentTypePage extends PageInf {

    @FindByFuzzyId("document_type_tree_form-create_document_type_button")
    private Link openCreateDialog;

    @FindBy(css = "#document_type_tree_form-document_type_categories_panel li")
    private List<WebElement> documentTypeClasses;

    @FindBy(xpath = "//body")
    public BillAttributes billAttributes;

    @FindBy(xpath = "//body")
    public BillTypeCreationDialog billTypeCreationDialog;

    @FindBy(xpath = "//body")
    public ContractTypeCreationDialog contractTypeCreationDialog;

    @FindBy(xpath = "//body")
    public ContractTypeAttributes contractTypeAttributes;

    @FindByFuzzyId("document_type_tree_form-document_type_tree")
    public Tree documentTypes;

    @FindBy(css = "#document_type_tree_form-document_type_controls i.fa-trash")
    private Link delete;

    public void openCreateDialog(String value) {
        openCreateDialog.click();

        documentTypeClasses.stream()
                .filter(item -> item.getText().equals(value))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("documentTypeClasses"))
                .click();
    }

    public void delete() {
        delete.click();
        currentDialog().click("Да");
    }
}