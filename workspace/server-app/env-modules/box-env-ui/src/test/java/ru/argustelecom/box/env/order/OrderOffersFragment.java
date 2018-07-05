package ru.argustelecom.box.env.order;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.List;

/**
 * Фрейм "Выбранные предложения" в карточе заявки
 */
public class OrderOffersFragment {

    @FindByFuzzyId("add_requirement")
    private Link openSelectRequirementsDialog;

    @FindBy(css = "#order_possible_offers_form-requirements ul.ui-datascroller-list li")
    private List<WebElement> requirements;

    @FindBy(css = "#order_possible_offers_form-order_possible_offers ul.ui-datascroller-list li")
    private List<WebElement> offers;

    // бесполезное поле, просто после выбора фильтров в диалоге нужно подождать, пока выполниться запрос, поэтому ждем
    // вот этот веб-элемент
    @FindBy(css = "#order_possible_offers_form-order_possible_offers ul.ui-datascroller-list li")
    private WebElement offer;

    @FindByFuzzyId("order_possible_offers_form-add_button")
    private Button add;

    @FindBy(css = "#order_offers_form li.ui-datascroller-item div.ui-dashboard-header span.fs16")
    private List<WebElement> chosenOffers;

    public void openSelectRequirementsDialog() {
        openSelectRequirementsDialog.click();
    }

    public void chooseRequirement(String value) {
        requirements.stream()
                .filter(item -> item.getText().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("requirements"))
                .findElement(By.cssSelector("div.ui-chkbox-box"))
                .click();
    }

    public void selectOffers(String value) {
        // в данном случае это не костыль т.к. весь диалог у нас уникальный и для него не подходит ни один из
        // компонентов, поэтому приходится явно ждать, пока отработает запрос после выставления фильтра
        Graphene.waitGui().until().element(offer).is().present();

        offers.stream()
                .filter(item -> item.getText().contains(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("offers"))
                .findElement(By.cssSelector("div.ui-chkbox-box"))
                .click();
    }

    public void addOffers() {
        add.click();
    }

    public String getChosenOfferPrice(String value) {

        WebElement chosenOffer = chosenOffers.stream()
                .filter(item -> item.getText().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("chosen offers"));

        return chosenOffer
                // название предложения и его стоимость - узлы одного уровня иерархии, поэтому найдя по названию
                // надо вернуться к родителю и обратиться к стоимости
                .findElement(By.xpath("//*/parent::*"))
                .findElement(By.cssSelector("span.money-component"))
                .getText();
    }

}
