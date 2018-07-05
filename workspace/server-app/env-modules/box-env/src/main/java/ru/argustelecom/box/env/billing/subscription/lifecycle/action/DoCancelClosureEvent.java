package ru.argustelecom.box.env.billing.subscription.lifecycle.action;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.queue.SubscriptionClosureHandler;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.queue.api.QueueProducer;

@LifecycleBean
public class DoCancelClosureEvent implements LifecycleCdiAction<SubscriptionState, Subscription> {

	@Inject
	private QueueProducer producer;

	@Override
	public void execute(ExecutionCtx<SubscriptionState, ? extends Subscription> ctx) {
		producer.remove(SubscriptionClosureHandler.genQueueName(ctx.getBusinessObject()));
	}
}
