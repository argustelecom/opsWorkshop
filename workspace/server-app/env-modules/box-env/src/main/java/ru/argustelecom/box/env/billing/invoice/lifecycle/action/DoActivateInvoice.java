package ru.argustelecom.box.env.billing.invoice.lifecycle.action;

import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

@LifecycleBean
public class DoActivateInvoice extends AbstractInvoiceProcessingAction {

	@Override
	public void execute(ExecutionCtx<InvoiceState, ? extends LongTermInvoice> ctx) {
		LongTermInvoice invoice = ctx.getBusinessObject();
		reserveFundsIfSupported(invoice);
	}

}
