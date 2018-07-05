package ru.argustelecom.box.env.home;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;

@Location("")
public class HomePage extends PageInf {

    @FindBy(css = "#layout_portlets_cover span")
    public WebElement greeting;

}
