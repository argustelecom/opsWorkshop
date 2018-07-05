package ru.argustelecom.box.env.billing.subscription.queue;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.subscription.SubscriptionProcessingService;
import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionRoutingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlerBean;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@QueueHandlerBean
@Named(value = SubscriptionClosureHandler.HANDLER_NAME)
public class SubscriptionClosureHandler implements QueueHandler {

	public static final String HANDLER_NAME = "subscriptionClosureHandler";

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private SubscriptionRoutingService routingSvc;
	
	@Inject
	private SubscriptionProcessingService processingSvc;

	@Override
	public QueueHandlingResult handleWork(QueueEvent event) throws Exception {
		SubscriptionContext context = event.getContext(SubscriptionContext.class);
		Subscription subscription = context.getSubscription(em);
		if (subscription.getValidTo() != null && DateUtils.before(subscription.getValidTo(), new Date())) {
			processingSvc.lock(subscription);
			routingSvc.close(subscription);
		}

		return QueueHandlingResult.SUCCESS;
	}

	@Override
	public QueueErrorResult handleError(QueueEvent event, QueueEventError error) throws Exception {
		return QueueErrorResult.RETRY_LATER;
	}

	public static String genQueueName(Subscription subscription) {
		return "CLOSURE_" + new EntityConverter().convertToString(subscription);
	}

	private static final long serialVersionUID = 1L;
}
