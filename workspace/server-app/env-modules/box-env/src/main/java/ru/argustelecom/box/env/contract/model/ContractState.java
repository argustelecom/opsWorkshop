package ru.argustelecom.box.env.contract.model;

import java.io.Serializable;
import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum ContractState implements LifecycleState<ContractState> {

	//@formatter:off
	REGISTRATION	("ContractRegistration"),
	INFORCE			("ContractInforce"),
	TERMINATED 		("ContractTerminated"),
	CANCELLED 		("ContractCancelled");
	//@formatter:on

	private String eventQualifier;

	@Override
	public Iterable<ContractState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public Serializable getKey() {
		return this;
	}

	@Override
	public String getName() {
		ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);

		switch (this) {
			case REGISTRATION:
				return messages.stateRegistration();
			case INFORCE:
				return messages.stateInforce();
			case TERMINATED:
				return messages.stateTerminated();
			case CANCELLED:
				return messages.stateCancelled();
			default:
				throw new SystemException("Unsupported ContractState");
		}
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}
}