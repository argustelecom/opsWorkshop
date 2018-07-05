package ru.argustelecom.box.env.queue;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/queue/QueueManagerView.xhtml")
public class QueueManagerPage extends PageInf {

    @FindBy(css = "i.fa-play-circle")
    public WebElement start;

    @FindByFuzzyId("queue_manager_form-queue_status")
    public OutputText status;

}
