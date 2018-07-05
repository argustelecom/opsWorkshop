package ru.argustelecom.box.env.task;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.box.env.filter.FilterFragment;

@Location("views/env/task/TaskListView.xhtml")
public class TaskListPage {

    @FindBy(xpath = "//body")
    public FilterFragment filterFragment;

}
