package ru.argustelecom.box.env.measure;

import org.jboss.arquillian.graphene.page.Location;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/measure/MeasureUnitDirectoryView.xhtml")
public class MeasureUnitDirectoryPage extends PageInf {

    @FindByFuzzyId("tree_buttons_form-create_measure")
    public Link openCreationDialog;

    @FindByFuzzyId("measure_unit_creation_form-code")
    public InputText code;

    @FindByFuzzyId("measure_unit_creation_form-value")
    public InputText name;

    @FindByFuzzyId("measure_unit_creation_form-symbol")
    public InputText symbol;

    @FindByFuzzyId("measure_unit_creation_form-factor")
    public InputText coefficient;

    @FindByFuzzyId("measure_unit_creation_form-measure_group_1_label")
    public ComboBox groups;

    @FindByFuzzyId("measure_unit_creation_form-create_measure_confirm")
    public Button create;

    @FindByFuzzyId("measure_filter-tree_model")
    public Tree units;

    @FindByFuzzyId("tree_buttons_form-delete_measure")
    private Link delete;

    public void delete() {
        delete.click();
        currentDialog().click("Да");
    }

}