package ru.argustelecom.box.env.type;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Table;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/type/LookupDirectoryView.xhtml")
public class LookupDirectoryPage extends PageInf {

    @FindByFuzzyId("lookup_category_form-create_category_button")
    public Link openCategoryCreationDialog;

    @FindByFuzzyId("lookup_category_form-categories")
    public Table categories;

    @FindBy(xpath = "//body")
    public CategoryCreationDialog categoryCreationDialog;

    class CategoryCreationDialog {

        @FindByFuzzyId("lookup_category_creation_form-name")
        public InputText name;

        @FindByFuzzyId("lookup_category_creation_form-description")
        public InputText description;

        @FindByFuzzyId("lookup_category_creation_form-lookup_category_create_button")
        public Button create;

    }

    @FindBy(css = "#entries_form-managed_buttons a:nth-of-type(1)")
    public Link openEntryCreationDialog;

    @FindBy(css = "#entries_form-managed_buttons a:nth-of-type(2)")
    public Link deactivate;

    @FindBy(xpath = "//body")
    public EntryCreationDialog entryCreationDialog;

    class EntryCreationDialog {

        @FindByFuzzyId("lookup_entry_creation_form-name")
        public InputText name;

        @FindByFuzzyId("lookup_entry_creation_form-description")
        public InputText description;

        @FindByFuzzyId("lookup_entry_creation_form-create_lookup_entry_button")
        public Button create;
    }

    @FindByFuzzyId("entries_form-entries")
    public Table entries;

}