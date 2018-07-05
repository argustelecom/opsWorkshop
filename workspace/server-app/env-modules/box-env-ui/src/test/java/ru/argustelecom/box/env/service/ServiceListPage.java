package ru.argustelecom.box.env.service;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.box.env.filter.FilterFragment;

@Location("views/env/services/ServiceListView.xhtml")
public class ServiceListPage {

    @FindBy(xpath = "//body")
    public FilterFragment filterFragment;

}
