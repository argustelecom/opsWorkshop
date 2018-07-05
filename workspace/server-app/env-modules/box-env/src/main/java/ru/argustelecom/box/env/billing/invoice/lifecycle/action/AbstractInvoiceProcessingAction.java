package ru.argustelecom.box.env.billing.invoice.lifecycle.action;

import static com.google.common.base.Preconditions.checkState;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.billing.account.PersonalAccountRepository;
import ru.argustelecom.box.env.billing.account.model.Reserve;
import ru.argustelecom.box.env.billing.invoice.UsageInvoiceSettingsRepository;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceSettings;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;

public abstract class AbstractInvoiceProcessingAction implements LifecycleCdiAction<InvoiceState, LongTermInvoice> {

	private static final Logger log = Logger.getLogger(AbstractInvoiceProcessingAction.class);

	@Inject
	private PersonalAccountRepository personalAccountRp;

	protected void reserveFundsIfSupported(LongTermInvoice invoice) {
		if (invoice.isReserveSupported()) {
			checkState(invoice.getReserve() == null);
			Reserve reserve = personalAccountRp.createReserve(invoice.getPersonalAccount(), invoice.getTotalPrice());
			invoice.attachReserve(reserve);
			log.debugv("Funds reserved for invoice {0}: {1}, {2}", invoice, reserve, reserve.getAmount());
		} else {
			log.debugv("Reservation of funds is not supported by invoice {0}", invoice);
		}
	}

	protected void cancelReservesIfSupported(LongTermInvoice invoice) {
		if (invoice.isReserveSupported()) {
			Reserve reserve = invoice.detachReserve();
			if (reserve != null) {
				personalAccountRp.removeReserve(reserve);
				log.debugv("Reservation of funds canceled for invoice {0}: {1}", invoice, reserve);
			}
		} else {
			log.debugv("Reservation of funds is not supported by invoice {0}", invoice);
		}
	}
}
