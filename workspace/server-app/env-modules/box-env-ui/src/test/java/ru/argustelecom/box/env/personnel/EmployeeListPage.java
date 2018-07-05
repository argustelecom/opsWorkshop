package ru.argustelecom.box.env.personnel;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/personnel/EmployeeListView.xhtml")
public class EmployeeListPage extends PageInf {

    @FindByFuzzyId("search_result-mainttp")
    public Button openCreateEmployeeDialog;

    @FindBy(xpath = "//body")
    public EmployeeInfoFragment employeeInfo;

}
