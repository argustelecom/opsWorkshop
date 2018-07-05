package ru.argustelecom.box.env.commodity.telephony.lifecycle.actions;

import static java.util.Collections.singletonList;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.ACTIVE;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.invoice.UsageInvoiceRepository;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionMediationService;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.chrono.ChronoUtils;

@LifecycleBean
public class DoDeactivateTelephonyOption implements LifecycleCdiAction<TelephonyOptionState, TelephonyOption> {

	@Inject
	private UsageInvoiceRepository usageInvoiceRp;

	@Inject
	private TelephonyOptionMediationService telephonyOptionMediationSvc;

	@Override
	public void execute(ExecutionCtx<TelephonyOptionState, ? extends TelephonyOption> ctx) {
		TelephonyOption option = ctx.getBusinessObject();
		final LocalDateTime now = LocalDateTime.now();
		final Date deactivationDate = ChronoUtils.fromLocalDateTime(now);

		usageInvoiceRp.createMissedInvoices(option, now);

		List<UsageInvoice> invoices = usageInvoiceRp.findInvoices(option, singletonList(ACTIVE), deactivationDate);

		invoices.forEach(i -> i.setEndDate(deactivationDate));

		telephonyOptionMediationSvc.updateEntries(option, deactivationDate);
	}

}