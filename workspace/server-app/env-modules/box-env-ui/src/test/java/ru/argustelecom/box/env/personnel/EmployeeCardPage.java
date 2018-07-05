package ru.argustelecom.box.env.personnel;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/personnel/EmployeeCardView.xhtml")
public class EmployeeCardPage extends PageInf {

    @FindByFuzzyId("employee_info_form-prefix")
    public OutputText prefix;

    @FindByFuzzyId("employee_info_form-last_name")
    public OutputText lastName;

    @FindByFuzzyId("employee_info_form-first_name")
    public OutputText firstName;

    @FindByFuzzyId("employee_info_form-second_name")
    public OutputText secondName;

    @FindByFuzzyId("employee_info_form-suffix")
    public OutputText suffix;

    @FindByFuzzyId("employee_info_form-appointment")
    public OutputText appointment;

    @FindByFuzzyId("employee_info_form-personnel_notes")
    public OutputText note;

    @FindByFuzzyId("employee_info_form-personnel_number")
    public OutputText number;

    @FindByFuzzyId("employee_login_form-create_account")
    public Link openCreateLoginDialog;

    @FindByFuzzyId("new_username")
    public InputText username;

    @FindByFuzzyId("new_email")
    public InputText email;

    @FindByFuzzyId("new_password")
    public InputText password;

    @FindByFuzzyId("new_confirm_password")
    public InputText confirmation;

    @FindByFuzzyId("new_description")
    public InputText description;

    @FindByFuzzyId("create_login_button")
    public Button createLogin;

    @FindByFuzzyId("employee_login_form-employee_login_out")
    public OutputText usernameOut;

    @FindByFuzzyId("employee_login_form-employee_login_email_out")
    public OutputText emailOut;

    @FindByFuzzyId("employee_login_form-employee_login_description_out")
    public OutputText descriptionOut;

    @FindByFuzzyId("fire_employee")
    public Link fire;

    @FindBy(css = ".warn-style")
    public WebElement fired;

    public void fire() {
        fire.click();
        currentDialog().click("Да");
    }
}