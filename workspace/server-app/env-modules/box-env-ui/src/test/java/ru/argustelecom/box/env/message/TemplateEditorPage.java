package ru.argustelecom.box.env.message;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;

@Location("views/env/mail/template/TemplateEditorView.xhtml")
public class TemplateEditorPage extends PageInf {

    @FindBy(css = "#templates_form-managed_buttons button:nth-child(1)")
    public Button openHint;

    @FindBy(css = "div.ui-overlaypanel")
    public WebElement hint;

}
