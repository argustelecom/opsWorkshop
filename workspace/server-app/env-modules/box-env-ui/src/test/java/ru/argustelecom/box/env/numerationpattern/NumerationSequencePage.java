package ru.argustelecom.box.env.numerationpattern;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.By;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/numerationpattern/NumerationPatternView.xhtml?activeTab=1")
public class NumerationSequencePage extends PageInf {

    @FindByFuzzyId("num_pattern_tab_view-sequence_search_result_form-create_sequence_button")
    public Button openCreateDialog;

    @FindByFuzzyId("sequence_edit_form-name")
    public InputText name;

    @FindByFuzzyId("sequence_edit_form-initial_value")
    public InputText initialValue;

    @FindByFuzzyId("sequence_edit_form-increment")
    public InputText increment;

    @FindByFuzzyId("sequence_edit_form-period_label")
    public ComboBox periods;

    @FindByFuzzyId("sequence_edit_form-capacity")
    public InputText capacity;

    @FindByFuzzyId("sequence_edit_form-submit_button")
    public Button create;

    @FindByFuzzyId("num_pattern_tab_view-sequence_search_result_form-sequence_table")
    public Table createdSequences;

    public void deleteSequence(String name) {
        int columnCount = createdSequences.getColumnCount();
        Row sequenceToDelete = createdSequences.findRow(name);

        sequenceToDelete
                .getCell(columnCount - 1)
                .findComponent(Link.class, By.cssSelector("i.fa-trash"))
                .click();

        currentDialog().click("Да");
    }
}
