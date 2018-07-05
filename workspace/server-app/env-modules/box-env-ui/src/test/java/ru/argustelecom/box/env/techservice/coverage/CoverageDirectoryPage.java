package ru.argustelecom.box.env.techservice.coverage;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/techservice/coverage/CoverageDirectoryView.xhtml")
public class CoverageDirectoryPage extends PageInf {

    @FindBy(css = "#coverage_form-managed_buttons a")
    public Link openCreationDialog;

    @FindByFuzzyId("coverage_edit_form-cancel_button")
    public Button cancel;

}
