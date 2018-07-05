package ru.argustelecom.box.env.personnel;

import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Calendar;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

public class EmployeeInfoFragment {

    @FindByFuzzyId("employee_edit_form-prefix")
    public InputText prefix;

    @FindByFuzzyId("employee_edit_form-last_name")
    public InputText lastName;

    @FindByFuzzyId("employee_edit_form-first_name")
    public InputText firstName;

    @FindByFuzzyId("employee_edit_form-second_name")
    public InputText secondName;

    @FindByFuzzyId("employee_edit_form-suffix")
    public InputText suffix;

    @FindByFuzzyId("employee_edit_form-new_appointment")
    public ComboBox appointments;

    @FindByFuzzyId("employee_edit_form-personnel_number")
    public InputText number;

    @FindByFuzzyId("employee_edit_form-personnel_notes")
    public InputText note;

    @FindByFuzzyId("employee_edit_form-save_person_edit_button")
    public Button create;

}
