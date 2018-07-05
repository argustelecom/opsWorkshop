package ru.argustelecom.box.env.billing.subscription;

import static ru.argustelecom.box.env.billing.subscription.queue.SubscriptionRenewingHandler.HANDLER_NAME;
import static ru.argustelecom.box.inf.queue.api.QueueProducer.Priority.LOW;

import java.util.Date;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import lombok.val;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.queue.SubscriptionRenewingContext;
import ru.argustelecom.box.env.billing.subscription.queue.SubscriptionRenewingHandler;
import ru.argustelecom.box.env.billing.transaction.event.TransactionCompletedEvent;
import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.box.inf.service.DomainService;

@DomainService
public class SubscriptionRenewingTxListener {

	@Inject
	private SubscriptionRepository subscriptionRp;

	@Inject
	private QueueProducer producer;

	@SuppressWarnings("unused")
	private void onTxCompleted(@Observes TransactionCompletedEvent event) {
		val tx = event.getTransaction();
		if (tx.getAmount().isPositive()) {
			val account = tx.getPersonalAccount();
			log.infov("Обнаружена транзакция пополнения лицевого счета {0}: {1}", account, tx.getAmount());
			if (hasSuspendedForDebtSubscriptions(account)) {
				log.info("Обнаружены подписки, приостановленные за неуплату...");
				scheduleRenewingEvent(account);
			}
		}
	}

	private boolean hasSuspendedForDebtSubscriptions(PersonalAccount account) {
		return !subscriptionRp.findSuspendedForDebtSubscriptions(account, false, 1).isEmpty();
	}

	private void scheduleRenewingEvent(PersonalAccount account) {
		String queueId = SubscriptionRenewingHandler.genQueueName(account);
		SubscriptionRenewingContext context = new SubscriptionRenewingContext(account);
		Date plannedTime = new Date(System.currentTimeMillis() + 60000);

		producer.schedule(queueId, null, LOW, plannedTime, HANDLER_NAME, context);
		log.infov("Запланировано задание {0} на автоматическое включение. Время выполнения: {1}", queueId, plannedTime);
	}

	private static final Logger log = Logger.getLogger(SubscriptionRenewingTxListener.class);
}
