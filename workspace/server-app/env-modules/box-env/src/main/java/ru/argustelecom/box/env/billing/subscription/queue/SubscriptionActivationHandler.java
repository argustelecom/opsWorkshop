package ru.argustelecom.box.env.billing.subscription.queue;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionRoutingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlerBean;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@QueueHandlerBean
@Named(value = SubscriptionActivationHandler.HANDLER_NAME)
public class SubscriptionActivationHandler implements QueueHandler {

	public static final String HANDLER_NAME = "subscriptionActivationHandler";

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private SubscriptionRoutingService routingSvc;

	@Override
	public QueueHandlingResult handleWork(QueueEvent event) throws Exception {
		SubscriptionContext context = event.getContext(SubscriptionContext.class);
		Subscription subscription = context.getSubscription(em);

		if (subscription.inState(SubscriptionState.ACTIVATION_WAITING)) {
			routingSvc.activate(subscription);
		}

		return QueueHandlingResult.SUCCESS;
	}

	@Override
	public QueueErrorResult handleError(QueueEvent event, QueueEventError error) throws Exception {
		return QueueErrorResult.RETRY_LATER;
	}

	public static String genQueueName(Subscription subscription) {
		return "ACTIVATION_" + new EntityConverter().convertToString(subscription);
	}

	private static final long serialVersionUID = 1L;
}
