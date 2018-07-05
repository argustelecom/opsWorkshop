package ru.argustelecom.box.env.commodity.telephony.lifecycle.actions;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.invoice.UsageInvoiceRepository;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionMediationService;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

import java.util.Date;

@LifecycleBean
public class DoActivateTelephonyOption implements LifecycleCdiAction<TelephonyOptionState, TelephonyOption> {

	@Inject
	private UsageInvoiceRepository usageInvoiceRp;

	@Inject
	private TelephonyOptionMediationService telephonyOptionMediationSrc;

	@Override
	public void execute(ExecutionCtx<TelephonyOptionState, ? extends TelephonyOption> ctx) {
		TelephonyOption option = ctx.getBusinessObject();
		Date now = new Date();
		usageInvoiceRp.createInvoice(option.getService(), option, now);
		telephonyOptionMediationSrc.createEntries(option, now);
	}

}
