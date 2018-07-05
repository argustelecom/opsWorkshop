package ru.argustelecom.box.env.commodity.telephony.model;

import java.io.Serializable;
import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Описание возможных состояний, в которых может находиться {@linkplain TelephonyOption опция телефонии}.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TelephonyOptionState implements LifecycleState<TelephonyOptionState> {

	//@formatter:off
	ACTIVE 		("TelephonyOptionActivation"),
	INACTIVE 	("TelephonyOptionSuspension");
	//@formatter:on

	private String eventQualifier;

	@Override
	public Iterable<TelephonyOptionState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public Serializable getKey() {
		return this;
	}

	@Override
	public String getName() {
		TelephonyOptionMessagesBundle messages = LocaleUtils.getMessages(TelephonyOptionMessagesBundle.class);
		switch (this) {
		case ACTIVE:
			return messages.stateActive();
		case INACTIVE:
			return messages.stateInactive();
		default:
			throw new SystemException("Unsupported TelephonyOptionState");
		}
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

}