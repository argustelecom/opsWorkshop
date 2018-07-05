package ru.argustelecom.box.env.billing.subscription.lifecycle.condition;

import java.util.Date;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiCondition;
import ru.argustelecom.box.env.lifecycle.api.context.TestingCtx;
import ru.argustelecom.system.inf.chrono.DateUtils;

@LifecycleBean
public class TestValidityDateDoesNotComingYet implements LifecycleCdiCondition<SubscriptionState, Subscription> {

	@Override
	public boolean test(TestingCtx<SubscriptionState, ? extends Subscription> ctx) {
		Date validFrom = ctx.getBusinessObject().getValidFrom();
		if (validFrom != null) {
			return DateUtils.after(validFrom, ctx.getExecutionDate());
		}
		return false;
	}
	
}
