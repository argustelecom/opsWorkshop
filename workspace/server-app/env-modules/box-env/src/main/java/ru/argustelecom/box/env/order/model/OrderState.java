package ru.argustelecom.box.env.order.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.order.nls.OrderMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

import java.io.Serializable;
import java.util.Arrays;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum OrderState implements LifecycleState<OrderState> {

	//@formatter:off
	FORMALIZATION 	("OrderFormalization"),
	IN_PROGRESS 	("OrderInProgress"),
	POSTPONED 		("OrderPostponed"),
	ARCHIVE 		("OrderArchive");
	//@formatter:on

	private String eventQualifier;

	@Override
	public Iterable<OrderState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public Serializable getKey() {
		return this;
	}

	@Override
	public String getName() {
		OrderMessagesBundle messages = LocaleUtils.getMessages(OrderMessagesBundle.class);

		switch (this) {
			case FORMALIZATION:
				return messages.stateFormalization();
			case IN_PROGRESS:
				return messages.stateInProgress();
			case POSTPONED:
				return messages.statePostponed();
			case ARCHIVE:
				return messages.stateArchive();
			default:
				throw new SystemException("Unsupported OrderState");
		}
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

}