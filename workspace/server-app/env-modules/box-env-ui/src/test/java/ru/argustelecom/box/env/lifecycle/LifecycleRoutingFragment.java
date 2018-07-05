package ru.argustelecom.box.env.lifecycle;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Checkbox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.*;

/**
 * Фрейм жизненого цикла
 */
public class LifecycleRoutingFragment {

    @FindByFuzzyId("main_route")
    private Button mainRoute;

    @FindByFuzzyId("main_route_among_others_button")
    private Button mainRouteAmongOthers;

    @FindByFuzzyId("main_route_among_others_menuButton")
    private Button secondaryRoutes;

    // Для обработки p:menuitem
    @FindBy(xpath = "//*[contains(@id,'main_route_among_others_menu')]/*/li")
    private List<WebElement> routes;

    @FindByFuzzyId("lifecycle_routing_dlg_form-ignore_warnings")
    private Checkbox gotIt;

    @FindBy(css = "#lifecycle_routing_dlg_form-lifecycle_routing_dlg textarea")
    private InputText comment;

    @FindByFuzzyId("lifecycle_routing_dlg_form-commit_routing")
    public Button confirm;

    /**
     * Метод ищет кнопку с названием перехода и совершает его
     * Метод следует использовать когда переход из текущего состояния ЖЦ единственный
     *
     * @param transition Название перехода
     */
    public void performSingleTransition(String transition) {
        if (transition.equals(mainRoute.getText())) {
            mainRoute.click();
        }
    }

    /**
     * Метод ищет кнопку с названием перехода и совершает его
     * Метод следует использовать когда переходов из текущего состояния ЖЦ больше одного
     *
     * @param transition Название перехода
     */
    public void performTransition(String transition) throws NoSuchElementException {
        if (transition.equals(mainRouteAmongOthers.getText())) {
            mainRouteAmongOthers.click();
            return;
        }

        secondaryRoutes.click();
        routes.stream()
                .filter(item -> transition.equals(item.getText()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("routes"))
                .click();
    }

    /**
     * Потверждаем и переход в диалоге подтверждения
     */
    public void confirmTransition() {
        Graphene.waitGui().until().element(By.cssSelector("#lifecycle_routing_dlg_form-lifecycle_routing_dlg")).is().visible();
        confirm.click();
    }

    public void setRead() {
        gotIt.check();
    }

    public void setComment(String comment) {
        this.comment.input(comment);
    }

}