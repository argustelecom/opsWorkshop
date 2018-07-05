package ru.argustelecom.box.env.billing.account.model;

import java.io.Serializable;
import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.billing.account.nls.PersonalAccountMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum PersonalAccountState implements LifecycleState<PersonalAccountState> {

	//@formatter:off
	ACTIVE	("PersonalAccountActive"),
	CLOSED	("PersonalAccountClosed");
	//@formatter:on

	private String eventQualifier;

	@Override
	public Iterable<PersonalAccountState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public String getName() {
		PersonalAccountMessagesBundle messages = LocaleUtils.getMessages(PersonalAccountMessagesBundle.class);
		switch (this) {
		case ACTIVE:
			return messages.stateActive();
		case CLOSED:
			return messages.stateClosed();
		default:
			throw new SystemException("Unsupported PersonalAccountState");
		}
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

	@Override
	public Serializable getKey() {
		return this;
	}

}