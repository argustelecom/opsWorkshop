package ru.argustelecom.box.env.billing.provision.model;

import ru.argustelecom.box.env.billing.provision.nls.ProvisionTermsMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

public enum PrematureActionPolicy {

	FACTUAL_PERIOD_COST,
	CHARGING_PERIOD_COST,
	ACCOUNTING_PERIOD_COST;

	public String getName() {
		ProvisionTermsMessagesBundle messages = LocaleUtils.getMessages(ProvisionTermsMessagesBundle.class);

		switch (this) {
			case FACTUAL_PERIOD_COST:
				return messages.prematureActionPolicyFactualPeriodCost();
			case CHARGING_PERIOD_COST:
				return messages.prematureActionPolicyChargingPeriodCost();
			case ACCOUNTING_PERIOD_COST:
				return messages.prematureActionPolicyAccountingPeriodCost();
			default:
				throw new SystemException("Unsupported PrematureActionPolicy");
		}
	}

	@Override
	public String toString() {
		return getName();
	}

}
