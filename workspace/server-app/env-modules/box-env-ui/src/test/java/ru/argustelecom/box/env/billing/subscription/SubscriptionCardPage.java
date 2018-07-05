package ru.argustelecom.box.env.billing.subscription;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.box.env.lifecycle.LifecycleHistoryFragment;
import ru.argustelecom.box.env.lifecycle.LifecycleRoutingFragment;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Table;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

/**
 * Страница: Домашняя страница -> Клиенты -> <Клиент> -> <Лицевой счёт> -> <Подписка>
 *
 * @author v.sysoev
 */

@Location("views/env/billing/subscription/SubscriptionCardView.xhtml")
public class SubscriptionCardPage extends PageInf {

    @FindBy(xpath = "//body")
    public LifecycleRoutingFragment lifecycleRoutingBlock;

    @FindBy(xpath = "//body")
    public LifecycleHistoryFragment lifecycleHistoryBlock;

    @FindByFuzzyId("subscription_attributes_form-state")
    public OutputText state;

    @FindByFuzzyId("invoice_entries_frame_form-invoices_table")
    public Table invoices;

    public String getLastInvoiceState() {
        return invoices.getRow(0).getCell(1).getTextString();
    }

}