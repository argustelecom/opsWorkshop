package ru.argustelecom.box.env.billing.subscription.queue;

import javax.persistence.EntityManager;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.api.context.EntityReference;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;

public class SubscriptionContext extends Context {

	private static final long serialVersionUID = -8106567331269489050L;

	private EntityReference<Subscription> subscriptionRef;

	protected SubscriptionContext(QueueEventImpl event) {
		super(event);
	}

	public SubscriptionContext(Subscription subscription) {
		super();
		subscriptionRef = new EntityReference<>(subscription);
	}

	public Subscription getSubscription(EntityManager em) {
		return subscriptionRef.get(em);
	}

}
