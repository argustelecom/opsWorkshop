package ru.argustelecom.box.env.commodity.model;

import java.io.Serializable;
import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.commodity.nls.ServiceMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Описание возможных состояний, в которых может находиться {@linkplain Service услуга}. Сейчас услуга может находиться
 * только в двух состояниях: активна и неактивна.
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ServiceState implements LifecycleState<ServiceState> {

	//@formatter:off
	ACTIVE 		("ServiceActivation"),
	INACTIVE 	("ServiceSuspension");
	//@formatter:on

	private String eventQualifier;

	@Override
	public Iterable<ServiceState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public Serializable getKey() {
		return this;
	}

	@Override
	public String getName() {
		ServiceMessagesBundle messages = LocaleUtils.getMessages(ServiceMessagesBundle.class);
			switch (this) {
				case ACTIVE:
					return messages.stateActive();
				case INACTIVE:
					return messages.stateInactive();
				default:
					throw new SystemException("Unsupported ServiceState");
			}
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

}