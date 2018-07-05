package ru.argustelecom.box.env.document.type;

import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Checkbox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

public class ContractTypeCreationDialog {

    @FindByFuzzyId("document_type_creation_form-new_doctype_name")
    public InputText name;

    @FindByFuzzyId("document_type_creation_form-new_doctype_desc")
    public InputText description;

    @FindByFuzzyId("document_type_creation_form-agency")
    public Checkbox isAgencyContractType;

    @FindByFuzzyId("document_type_creation_form-provider_label")
    public ComboBox providers;

    @FindByFuzzyId("document_type_creation_form-new_doctype_customer_type_label")
    public ComboBox customerTypes;

    @FindByFuzzyId("document_type_creation_form-create_button")
    public Button create;
}