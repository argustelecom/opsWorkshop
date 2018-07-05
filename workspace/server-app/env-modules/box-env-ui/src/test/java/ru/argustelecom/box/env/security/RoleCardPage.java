package ru.argustelecom.box.env.security;

import org.jboss.arquillian.graphene.page.Location;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/security/RoleCardView.xhtml")
public class RoleCardPage {

    @FindByFuzzyId("role_attrib_form-role_name_out")
    public OutputText name;

    @FindByFuzzyId("role_attrib_form-role_desc_out")
    public OutputText description;

    @FindByFuzzyId("role_attrib_form-remove_role")
    public Button removeRole;

}