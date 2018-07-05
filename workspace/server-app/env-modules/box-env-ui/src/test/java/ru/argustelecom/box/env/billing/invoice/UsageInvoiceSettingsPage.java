package ru.argustelecom.box.env.billing.invoice;

import org.jboss.arquillian.graphene.page.Location;
import ru.argustelecom.system.inf.testframework.it.ui.comp.EditableSection;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/billing/invoice/UsageInvoiceSettingsView.xhtml")
public class UsageInvoiceSettingsPage {

    @FindByFuzzyId("usage_invoice_settings-schedule")
    public EditableSection scheduleSettings;

    @FindByFuzzyId("usage_invoice_settings-schedule_amount")
    public InputText scheduleUnitAmount;
}
