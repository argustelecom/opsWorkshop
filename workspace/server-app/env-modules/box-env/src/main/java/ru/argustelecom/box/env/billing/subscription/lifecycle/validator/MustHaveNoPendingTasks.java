package ru.argustelecom.box.env.billing.subscription.lifecycle.validator;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveNoPendingTasks implements LifecycleCdiValidator<SubscriptionState, Subscription> {

	@Override
	public void validate(ExecutionCtx<SubscriptionState, ? extends Subscription> ctx, ValidationResult<Object> result) {
		// TODO Проверить, что закрыт таск после мержа
	}

}
