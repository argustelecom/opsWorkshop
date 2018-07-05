package ru.argustelecom.box.env.billing.subscription.lifecycle.action;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

@LifecycleBean
public class DoSetValidTo implements LifecycleCdiAction<SubscriptionState, Subscription> {

	@Override
	public void execute(ExecutionCtx<SubscriptionState, ? extends Subscription> ctx) {
		Subscription subscription = ctx.getBusinessObject();
		if (subscription.getValidTo() == null) {
			subscription.setValidTo(ctx.getExecutionDate());
		}
	}
}
