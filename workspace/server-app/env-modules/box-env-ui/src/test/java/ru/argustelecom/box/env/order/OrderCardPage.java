package ru.argustelecom.box.env.order;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.box.env.customer.CustomerFragment;
import ru.argustelecom.box.env.lifecycle.LifecycleRoutingFragment;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

/**
 * Карточка заявки
 * Домашняя страницы -> Работа с клиентами -> Заявки -> <Заявка>
 */
@Location("views/env/order/OrderCardView.xhtml")
public class OrderCardPage extends PageInf {

    @FindBy(xpath = "//body")
    public OrderOffersFragment orderOffersBlock;

    @FindBy(xpath = "//body")
    public CustomerFragment customerBlock;

    @FindBy(xpath = "//body")
    public LifecycleRoutingFragment lifecycleRoutingBlock;

    @FindByFuzzyId("order_attributes_form-state_out")
    public OutputText state;

}
