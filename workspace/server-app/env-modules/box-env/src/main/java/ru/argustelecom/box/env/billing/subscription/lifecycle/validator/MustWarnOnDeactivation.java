package ru.argustelecom.box.env.billing.subscription.lifecycle.validator;

import lombok.val;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.nls.SubscriptionMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustWarnOnDeactivation implements LifecycleCdiValidator<SubscriptionState, Subscription> {

	@Override
	public void validate(ExecutionCtx<SubscriptionState, ? extends Subscription> ctx, ValidationResult<Object> result) {
		val subscription = ctx.getBusinessObject();
		val subscriptionState = ctx.getEndpoint().getDestination();

		SubscriptionMessagesBundle messages = LocaleUtils.getMessages(SubscriptionMessagesBundle.class);
		result.warnv(subscription, messages.deactivationWarn(subscriptionState.getName()));
	}

}
