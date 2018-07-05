package ru.argustelecom.box.env.billing.invoice.model;

import static java.util.Arrays.asList;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.io.Serializable;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.billing.invoice.nls.ChargeJobMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum ChargeJobState implements LifecycleState<ChargeJobState> {

	//@formatter:off
	FORMALIZATION 			("ChargeJobFormalization", ChargeJobMessagesBundle::stateFormalization),
	PERFORMED_PRE_BILLING 	("ChargeJobPerformedInPreBilling", ChargeJobMessagesBundle::statePerformedPreBilling),
	SYNCHRONIZATION 		("ChargeJobSynchronization", ChargeJobMessagesBundle::stateSynchronization),
	SYNCHRONIZED			("ChargeJobSynchronized", ChargeJobMessagesBundle::stateSynchronized),
	PERFORMED_BILLING 		("ChargeJobPerformedInBilling", ChargeJobMessagesBundle::statePerformedBilling),
	DONE					("ChargeJobDone", ChargeJobMessagesBundle::stateDone),
	ABORTED					("ChargeJobAborted", ChargeJobMessagesBundle::stateAborted);
	//@formatter:on

	private String eventQualifier;
	private Function<ChargeJobMessagesBundle, String> nameGetter;

	@Override
	public Iterable<ChargeJobState> getStates() {
		return asList(values());
	}

	@Override
	public Serializable getKey() {
		return this;
	}

	@Override
	public String getName() {
		return nameGetter.apply(getMessages(ChargeJobMessagesBundle.class));
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

}