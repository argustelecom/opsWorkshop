package ru.argustelecom.box.env.commodity;

import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

class CreationDialog {

    @FindByFuzzyId("commodity_type_creation_form-create_commodity_type_button")
    public Button create;

    @FindByFuzzyId("commodity_type_creation_form-name")
    public InputText name;

    @FindByFuzzyId("commodity_type_creation_form-keyword")
    public InputText keyword;

    @FindByFuzzyId("commodity_type_creation_form-group_label")
    public ComboBox groups;
}