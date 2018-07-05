package ru.argustelecom.box.env.billing.provision.model;

import ru.argustelecom.box.env.billing.provision.nls.ProvisionTermsMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

public enum RoundingPolicy {

	//@formatter:off
	UP,
	DOWN;
	//@formatter:on

	public String getName() {
		ProvisionTermsMessagesBundle messages = LocaleUtils.getMessages(ProvisionTermsMessagesBundle.class);

		switch (this) {
			case UP:
				return messages.roundingPolicyUp();
			case DOWN:
				return messages.roundingPolicyDown();
			default:
				throw new SystemException("Unsupported RoundingPolicy");
		}
	}

	@Override
	public String toString() {
		return getName();
	}

}