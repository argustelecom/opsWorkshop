package ru.argustelecom.box.env.telephony.tariff.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Arrays;

import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum TariffState implements LifecycleState<TariffState> {

	//@formatter:off
	FORMALIZATION ("TariffFormalization"),
	ACTIVE 		  ("TariffActive"),
	ARCHIVE       ("TariffArchive"),
	CANCELLED     ("TariffCancelled");
	//@formatter:on

	private String eventQualifier;

	@Override
	public Iterable<TariffState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public Serializable getKey() {
		return this;
	}

	@Override
	public String getName() {
		TariffMessagesBundle messages = LocaleUtils.getMessages(TariffMessagesBundle.class);

		switch (this) {
			case FORMALIZATION:
				return messages.stateFormalization();
			case ACTIVE:
				return messages.stateActive();
			case ARCHIVE:
				return messages.stateArchive();
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
