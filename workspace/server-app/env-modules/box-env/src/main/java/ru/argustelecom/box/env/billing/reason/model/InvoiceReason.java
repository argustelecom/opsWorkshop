package ru.argustelecom.box.env.billing.reason.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.reason.nls.ReasonMessagesBundle;
import ru.argustelecom.box.env.billing.invoice.model.ShortTermInvoice;
import ru.argustelecom.box.env.billing.transaction.model.TransactionReason;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@Entity
@Access(AccessType.FIELD)
public class InvoiceReason extends TransactionReason {

	private static final long serialVersionUID = -3195840996691805466L;

	@OneToOne(fetch = FetchType.LAZY)
	private AbstractInvoice invoice;

	protected InvoiceReason() {
	}

	protected InvoiceReason(Long id) {
		super(id);
	}

	public InvoiceReason(Long id, AbstractInvoice invoice) {
		super(id);
		this.invoice = invoice;
	}

	@Override
	public String getDescription() {
		ReasonMessagesBundle messages = LocaleUtils.getMessages(ReasonMessagesBundle.class);
		StringBuilder descriptionBuilder = new StringBuilder(messages.usage()).append(" (");
		if (invoice instanceof ShortTermInvoice) {
			((ShortTermInvoice) invoice).getEntries().forEach(entry -> descriptionBuilder
					.append(entry.getProductOffering().getProductType().getObjectName()).append(", "));
		} else if (invoice instanceof LongTermInvoice) {
			descriptionBuilder.append(((LongTermInvoice) invoice).getSubscription().getObjectName());
		}
		descriptionBuilder.delete(descriptionBuilder.length() - 2, descriptionBuilder.length());
		descriptionBuilder.append("). ");
		descriptionBuilder.append(messages.invoice()).append(" #").append(invoice.getId());
		return descriptionBuilder.toString();
	}

	@Override
	public String getReasonType() {
		ReasonMessagesBundle messages = LocaleUtils.getMessages(ReasonMessagesBundle.class);
		return messages.invoice();
	}

	@Override
	public String getReasonNumber() {
		return invoice.getId().toString();
	}
}
