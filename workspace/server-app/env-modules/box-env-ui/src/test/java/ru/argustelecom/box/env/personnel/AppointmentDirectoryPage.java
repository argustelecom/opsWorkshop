package ru.argustelecom.box.env.personnel;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Table;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/personnel/AppointmentDirectoryView.xhtml")
public class AppointmentDirectoryPage {

    @FindBy(css = "#appointments_form i.fa-edit")
    public Link openCreationDialog;

    @FindBy(css = "input[id$='new_name']")
    public InputText name;

    @FindBy(css = "button[id$='create_button']")
    public Button create;

    @FindByFuzzyId("appointments_form-appointments_table")
    public Table appointmentTable;

}