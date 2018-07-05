package ru.argustelecom.box.env.billing.invoice.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.billing.invoice.nls.InvoiceMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

import java.io.Serializable;
import java.util.Arrays;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum InvoiceState implements LifecycleState<InvoiceState> {

	//@formatter:off
	CREATED 	("InvoiceCreated"),
	ACTIVE 		("InvoiceActive"),
	CLOSED 		("InvoiceClosed"),
	CANCELLED 	("InvoiceCancelled");
	//@formatter:on

	private String eventQualifier;

	@Override
	public Iterable<InvoiceState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public Serializable getKey() {
		return this;
	}

	@Override
	public String getName() {

		InvoiceMessagesBundle messages = LocaleUtils.getMessages(InvoiceMessagesBundle.class);

		switch (this) {
			case CREATED:
				return messages.stateCreated();
			case ACTIVE:
				return messages.stateActive();
			case CLOSED:
				return messages.stateClosed();
			case CANCELLED:
				return messages.stateCancelled();
			default:
				throw new SystemException("Unsupported LongTermInvoiceLifecycle.InvoiceState");
		}
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

}