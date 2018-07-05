package ru.argustelecom.box.env.billing.provision.model;

import java.io.Serializable;
import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.billing.provision.nls.ProvisionTermsMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.publang.billing.model.IRecurrentTerms.State;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum RecurrentTermsState implements LifecycleState<RecurrentTermsState> {

	//@formatter:off
	FORMALIZATION	(State.FORMALIZATION),
	ACTIVE			(State.ACTIVE),
	ARCHIVE			(State.ARCHIVE);
	//@formatter:on

	private String eventQualifier;

	@Override
	public Iterable<RecurrentTermsState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public Serializable getKey() {
		return this;
	}

	@Override
	public String getName() {
		ProvisionTermsMessagesBundle messages = LocaleUtils.getMessages(ProvisionTermsMessagesBundle.class);

		switch (this) {
			case FORMALIZATION:
				return messages.stateFormalization();
			case ACTIVE:
				return messages.stateActive();
			case ARCHIVE:
				return messages.stateClosed();
			default:
				throw new SystemException("Unsupported RecurrentTermsState");
		}
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

}