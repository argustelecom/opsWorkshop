package ru.argustelecom.box.env.contact;

import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;

import java.util.List;

public class ContactBlockFragment {

    @FindBy(css = "#employee_edit_form-contact_edit_frame_panel input[role=textbox]")
    private List<InputText> contactValues;

    @FindBy(css = "#employee_edit_form-contact_edit_frame_panel label")
    private ComboBox contactTypes;

}