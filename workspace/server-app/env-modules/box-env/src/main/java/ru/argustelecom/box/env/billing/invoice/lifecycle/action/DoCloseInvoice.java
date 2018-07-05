package ru.argustelecom.box.env.billing.invoice.lifecycle.action;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.transaction.TransactionRepository;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.stl.Money;

@LifecycleBean
public class DoCloseInvoice extends AbstractInvoiceProcessingAction {

	private static final Logger log = Logger.getLogger(DoCloseInvoice.class);

	@Inject
	private TransactionRepository transactionRp;

	@Override
	public void execute(ExecutionCtx<InvoiceState, ? extends LongTermInvoice> ctx) {
		LongTermInvoice invoice = ctx.getBusinessObject();

		cancelReservesIfSupported(invoice);
		createTransaction(invoice);
	}

	private void createTransaction(LongTermInvoice invoice) {
		if (invoice.getTotalPrice().compareTo(Money.ZERO) > 0) {
			Transaction tx = transactionRp.createLongTermInvoiceTransaction(invoice);
			invoice.joinTransaction(tx);
			log.debugv("Created financial transaction {0} for invoice {1}", tx, invoice);
		} else {
			log.warnv("Invoice {1} has zero cost. Financial transaction is not created!", invoice);
		}
	}
}
