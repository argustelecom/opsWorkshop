package ru.argustelecom.box.env.security;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/security/RoleListView.xhtml")
public class RoleListPage {

    @FindByFuzzyId("search_result_form-mainttp")
    public Button openCreateDialog;

    @FindByFuzzyId("role_creation_form-role_name")
    public InputText name;

    @FindByFuzzyId("role_creation_form-role_desc")
    public InputText description;

    @FindBy(css = "#role_creation_form button:nth-child(2)")
    public Button create;

}