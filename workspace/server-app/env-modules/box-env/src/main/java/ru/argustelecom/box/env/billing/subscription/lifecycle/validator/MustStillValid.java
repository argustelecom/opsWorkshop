package ru.argustelecom.box.env.billing.subscription.lifecycle.validator;

import java.util.Date;

import lombok.val;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.nls.SubscriptionMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustStillValid implements LifecycleCdiValidator<SubscriptionState, Subscription> {

	@Override
	public void validate(ExecutionCtx<SubscriptionState, ? extends Subscription> ctx, ValidationResult<Object> result) {
		val subscription = ctx.getBusinessObject();
		val currentDate = new Date();
		if (subscription.getValidTo() != null && DateUtils.after(currentDate, subscription.getValidTo())) {
			SubscriptionMessagesBundle messages = LocaleUtils.getMessages(SubscriptionMessagesBundle.class);
			// FIXME toString() ?
			result.errorv(subscription, messages.isExpired(subscription.getValidTo().toString()));
		}
	}

}
