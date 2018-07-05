package ru.argustelecom.box.env.billing.subscription.model;

import java.io.Serializable;
import java.util.Arrays;

import ru.argustelecom.box.env.billing.subscription.nls.SubscriptionMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.publang.billing.model.ISubscription;
import ru.argustelecom.system.inf.exception.SystemException;

public enum SubscriptionState implements LifecycleState<SubscriptionState> {

	//@formatter:off
	FORMALIZATION				 (ISubscription.State.FORMALIZATION, false),
	ACTIVATION_WAITING			 (ISubscription.State.ACTIVATION_WAITING, false),
	ACTIVE						 (ISubscription.State.ACTIVE, true),
	SUSPENSION_FOR_DEBT_WAITING  (ISubscription.State.SUSPENSION_FOR_DEBT_WAITING, false),
	SUSPENSION_ON_DEMAND_WAITING (ISubscription.State.SUSPENSION_ON_DEMAND_WAITING, false),
	SUSPENDED_FOR_DEBT 			 (ISubscription.State.SUSPENDED_FOR_DEBT, false),
	SUSPENDED_ON_DEMAND			 (ISubscription.State.SUSPENDED_ON_DEMAND, false),
	SUSPENDED					 (ISubscription.State.SUSPENDED, false),
	CLOSURE_WAITING 			 (ISubscription.State.CLOSURE_WAITING, false),
	CLOSED						 (ISubscription.State.CLOSED, false);
	//@formatter:on

	private String eventQualifier;
	private boolean chargeable;

	SubscriptionState(String eventQualifier, boolean chargeable) {
		this.eventQualifier = eventQualifier;
		this.chargeable = chargeable;
	}

	public static SubscriptionState findByName(String name) {
		return Arrays.stream(values()).filter(state -> state.getName().equals(name)).findFirst().orElse(null);
	}

	@Override
	public Iterable<SubscriptionState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public String getName() {

		SubscriptionMessagesBundle messages = LocaleUtils.getMessages(SubscriptionMessagesBundle.class);

		switch (this) {
			case FORMALIZATION:
				return messages.stateFormalization();
			case ACTIVATION_WAITING:
				return messages.stateActivationWaiting();
			case ACTIVE:
				return messages.stateActive();
			case SUSPENSION_FOR_DEBT_WAITING:
				return messages.stateSuspensionForDebtWaiting();
			case SUSPENSION_ON_DEMAND_WAITING:
				return messages.stateSuspensionOnDemandWaiting();
			case SUSPENDED_ON_DEMAND:
				return messages.stateSuspendedOnDemand();
			case SUSPENDED_FOR_DEBT:
				return messages.stateSuspendedForDebt();
			case SUSPENDED:
				return messages.stateSuspended();
			case CLOSURE_WAITING:
				return messages.stateClosureWaiting();
			case CLOSED:
				return messages.stateClosed();
			default:
				throw new SystemException("Unsupported SubscriptionState");
		}
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

	public boolean isChargeable() {
		return chargeable;
	}

	@Override
	public Serializable getKey() {
		return this;
	}
}