package ru.argustelecom.box.env.contract.lifecycle.validator;

import javax.inject.Inject;

import lombok.val;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.contract.lifecycle.model.SubscriptionCreationPresets;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveSubscriptionForEachEntry implements LifecycleCdiValidator<ContractState, AbstractContract<?>> {

	@Inject
	private SubscriptionRepository subscriptionRepository;

	@Override
	public void validate(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx,
			ValidationResult<Object> result) {

		val abstractContract = ctx.getBusinessObject();
		val builder = new StringBuilder();
		boolean validationFailed = false;

		for (val entry : abstractContract.getProductOfferingEntries()) {
			if (!entry.getProductOffering().isRecurrentProduct()) {
				continue;
			}

			Subscription subscription = subscriptionRepository.findSubscription(entry);
			val explicitSpecified = subscription != null;
			val implicitSpecified = ctx.hasData(entry, SubscriptionCreationPresets.class);

			if (!explicitSpecified && !implicitSpecified) {
				if (validationFailed) {
					builder.append(", ");
				} else {
					validationFailed = true;
				}
				builder.append(entry.getProductOffering().getObjectName());
			}
		}

		if (validationFailed) {
			ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);
			result.errorv(abstractContract, messages.subscriptionsAreNotSpecified(builder.toString()));
		}
	}

}
