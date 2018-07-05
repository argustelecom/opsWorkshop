package ru.argustelecom.box.env.billing.subscription.lifecycle.validator;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.nls.SubscriptionMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveDefinedValidFrom implements LifecycleCdiValidator<SubscriptionState, Subscription> {

	@Override
	public void validate(ExecutionCtx<SubscriptionState, ? extends Subscription> ctx, ValidationResult<Object> result) {
		Subscription subscription = ctx.getBusinessObject();
		if (subscription.getValidFrom() == null) {
			SubscriptionMessagesBundle messages = LocaleUtils.getMessages(SubscriptionMessagesBundle.class);
			result.error(subscription, messages.validFromIsNotSpecified());
		}
	}
}
