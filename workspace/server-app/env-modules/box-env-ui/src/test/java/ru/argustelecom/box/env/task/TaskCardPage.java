package ru.argustelecom.box.env.task;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/task/TaskCardView.xhtml")
public class TaskCardPage extends PageInf {

    @FindBy(css = "#task_attributes_form button:nth-child(1)")
    public Button openAssignDialog;

    @FindByFuzzyId("assignee_selection_form-task_assignee_label")
    public ComboBox assignees;

    @FindByFuzzyId("assignee_selection_form-assign_button")
    public Button assign;

    @FindByFuzzyId("task_attributes_form-task_assignee")
    public OutputText assignee;

}
