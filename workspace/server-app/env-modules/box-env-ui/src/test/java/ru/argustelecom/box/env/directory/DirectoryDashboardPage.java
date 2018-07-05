package ru.argustelecom.box.env.directory;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;

import java.util.ArrayList;
import java.util.List;

@Location("views/env/directory/DirectoryDashboardView.xhtml")
public class DirectoryDashboardPage extends PageInf {

    @FindBy(css = "#page_path_form li span")
    public List<WebElement> breadcrumbs;

    public List<String> getBreadcrumbsNames() {
        List<String> breadcrumbsNames = new ArrayList<>();

        breadcrumbs.forEach(item -> breadcrumbsNames.add(item.getText()));

        return breadcrumbsNames;
    }

}
