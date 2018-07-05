package ru.argustelecom.box.env.party;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/party/CustomerSegmentDirectoryView.xhtml")
public class CustomerSegmentDirectoryPage extends PageInf {

    @FindBy(css = "#customer_segments_form-managed_buttons i.fa-edit")
    public Link openCreationDialog;

    @FindByFuzzyId("customer_segment_edit_dlg_form-segment_customer_type_label")
    public ComboBox customerTypes;

    @FindByFuzzyId("customer_segment_edit_dlg_form-segment_name")
    public InputText name;

    @FindByFuzzyId("customer_segment_edit_dlg_form-segment_desc")
    public InputText description;

    @FindByFuzzyId("customer_segment_edit_dlg_form-create_button")
    public Button create;

    @FindByFuzzyId("customer_segments_form-customer_segments_table")
    public Table segmentsTable;

    @FindByFuzzyId("customer_segments_form-remove_button")
    private Link delete;

    public void delete() {
        delete.click();
        currentDialog().click("Да");
    }
}