package ru.argustelecom.box.env.billing.invoice.lifecycle.action;

import lombok.val;

import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.transaction.TransactionRepository;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.stl.Money;

import javax.inject.Inject;

import org.jboss.logging.Logger;

@LifecycleBean
public class DoCloseUsageInvoice implements LifecycleCdiAction<InvoiceState, UsageInvoice> {

	private static final Logger log = Logger.getLogger(DoCloseInvoice.class);

	@Inject
	private SubscriptionRepository subscriptionRp;

	@Inject
	private TransactionRepository transactionRp;

	@Override
	public void execute(ExecutionCtx<InvoiceState, ? extends UsageInvoice> ctx) {
		val invoice = ctx.getBusinessObject();
		createTransaction(invoice);
	}

	private void createTransaction(UsageInvoice invoice) {
		if (invoice.getTotalPrice().compareTo(Money.ZERO) > 0) {
			Transaction tx = transactionRp.createUsageInvoiceTransaction(invoice);
			invoice.joinTransaction(tx);
			log.debugv("Created financial transaction {0} for invoice {1}", tx, invoice);
		} else {
			log.warnv("Invoice {1} has zero cost. Financial transaction is not created!", invoice);
		}
	}
}
