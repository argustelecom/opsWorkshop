package ru.argustelecom.box.env.billing.subscription.queue;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRenewingService;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlerBean;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@QueueHandlerBean
@Named(value = SubscriptionRenewingHandler.HANDLER_NAME)
public class SubscriptionRenewingHandler implements QueueHandler {

	public static final String HANDLER_NAME = "subscriptionRenewingHandler";

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private SubscriptionRenewingService renewingSvc;

	@Override
	public QueueHandlingResult handleWork(QueueEvent event) throws Exception {
		SubscriptionRenewingContext context = event.getContext(SubscriptionRenewingContext.class);
		PersonalAccount account = context.getPersonalAccount(em);
		renewingSvc.tryRenewAllOnDebtSuspension(account);
		return QueueHandlingResult.SUCCESS;
	}

	@Override
	public QueueErrorResult handleError(QueueEvent event, QueueEventError error) throws Exception {
		return error.getAttemptsCount() <= 3 ? QueueErrorResult.RETRY_LATER : QueueErrorResult.REJECT_EVENT;
	}

	public static String genQueueName(PersonalAccount account) {
		return "RENEWING_" + new EntityConverter().convertToString(account);
	}

	private static final long serialVersionUID = 4726000447728533552L;
}
