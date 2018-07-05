package ru.argustelecom.box.env.pricing.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.pricing.nls.PricelistMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

import java.io.Serializable;
import java.util.Arrays;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum PricelistState implements LifecycleState<PricelistState> {

	//@formatter:off
	CREATED ("PricelistCreated"),
	INFORCE ("PricelistInforce"),
	CLOSED ("PricelistClosed"),
	CANCELLED ("PricelistCancelled");
	//@formatter:on

	private String eventQualifier;

	@Override
	public Iterable<PricelistState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public Serializable getKey() {
		return this;
	}

	@Override
	public String getName() {
		PricelistMessagesBundle messages = LocaleUtils.getMessages(PricelistMessagesBundle.class);

		switch (this) {
			case CREATED:
				return messages.stateCreated();
			case INFORCE:
				return messages.stateInforce();
			case CLOSED:
				return messages.stateClosed();
			case CANCELLED:
				return messages.stateCancelled();
			default:
				throw new SystemException("Unsupported PricelistState");
		}
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

}