package ru.argustelecom.box.env.billing.subscription.lifecycle.action;

import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.FORMALIZATION;
import static ru.argustelecom.box.inf.queue.api.QueueProducer.Priority.MEDIUM;

import java.util.Date;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.queue.SubscriptionActivationHandler;
import ru.argustelecom.box.env.billing.subscription.queue.SubscriptionContext;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.system.inf.chrono.DateUtils;

@LifecycleBean
public class DoScheduleActivationEvent implements LifecycleCdiAction<SubscriptionState, Subscription> {

	@Inject
	private QueueProducer producer;

	@Override
	public void execute(ExecutionCtx<SubscriptionState, ? extends Subscription> ctx) {
		Subscription subscription = ctx.getBusinessObject();
		if (subscription.inState(FORMALIZATION)) {
			Date activationDate = subscription.getValidFrom();
			if (activationDate != null && DateUtils.after(activationDate, new Date())) {
				SubscriptionContext eventCtx = new SubscriptionContext(subscription);
				String queueName = SubscriptionActivationHandler.genQueueName(subscription);

				producer.remove(queueName);
				producer.schedule(queueName, null, MEDIUM, activationDate, SubscriptionActivationHandler.HANDLER_NAME,
						eventCtx);
			}
		}
	}
}
