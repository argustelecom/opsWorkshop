package ru.argustelecom.box.env.billing.subscription.lifecycle.action;

import java.util.Date;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.subscription.SubscriptionProcessingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

@LifecycleBean
public class DoCloseInvoiceOnRouting implements LifecycleCdiAction<SubscriptionState, Subscription> {

	@Inject
	private SubscriptionProcessingService processor;

	@Override
	public void execute(ExecutionCtx<SubscriptionState, ? extends Subscription> ctx) {
		Subscription subscription = ctx.getBusinessObject();
		SubscriptionState toState = ctx.getEndpoint().getDestination();
		Date executionDate = ctx.getExecutionDate();

		processor.closeInvoiceOnRouting(subscription, toState, executionDate);
	}
}
