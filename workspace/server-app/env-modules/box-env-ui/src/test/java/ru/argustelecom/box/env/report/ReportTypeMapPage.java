package ru.argustelecom.box.env.report;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;

import java.util.ArrayList;
import java.util.List;

@Location("views/env/report/ReportTypeMapView.xhtml")
public class ReportTypeMapPage extends PageInf {

    @FindBy(css = ".ui-dashboard-header span")
    private List<WebElement> groups;

    public List<String> getGroups() {
        List<String> groupNames = new ArrayList<>();

        groups.forEach(item -> groupNames.add(item.getText()));

        return groupNames;
    }
}
