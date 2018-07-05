package ru.argustelecom.box.env.billing.subscription.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.subscription.nls.SubscriptionMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.NamedObject;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum SubscriptionLifecycleQualifier implements NamedObject, Comparable<SubscriptionLifecycleQualifier> {

	FULL("/box-env/images/long_lifecycle.svg"),
	SHORT("/box-env/images/short_lifecycle.svg");

	@Getter
	private String pathToSchema;

	@Override
	public String getObjectName() {
		return getName();
	}

	public String getName() {
		SubscriptionMessagesBundle messages = LocaleUtils.getMessages(SubscriptionMessagesBundle.class);

		switch (this) {
			case FULL:
				return messages.lifecycleQualifierFull();
			case SHORT:
				return messages.lifecycleQualifierShort();
			default:
				throw new SystemException("Unsupported SubscriptionLifecycleQualifier#name");
		}
	}

	public String getHeaderTitle() {
		SubscriptionMessagesBundle messages = LocaleUtils.getMessages(SubscriptionMessagesBundle.class);

		switch (this) {
			case FULL:
				return messages.schemeTitleFull();
			case SHORT:
				return messages.schemeTitleShort();
			default:
				throw new SystemException("Unsupported SubscriptionLifecycleQualifier#headerTitle");
		}
	}

	public static SubscriptionLifecycleQualifier[] availableSubscriptionLifecycleQualifiers() {
		return values();
	}

}