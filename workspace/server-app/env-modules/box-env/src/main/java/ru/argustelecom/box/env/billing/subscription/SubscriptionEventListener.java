package ru.argustelecom.box.env.billing.subscription;

import static ru.argustelecom.box.publang.billing.model.ISubscription.State.ACTIVE;
import static ru.argustelecom.box.publang.billing.model.ISubscription.State.CLOSURE_WAITING;
import static ru.argustelecom.box.publang.billing.model.ISubscription.State.SUSPENSION_FOR_DEBT_WAITING;
import static ru.argustelecom.box.publang.billing.model.ISubscription.State.SUSPENSION_ON_DEMAND_WAITING;

import java.util.Date;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.subscription.model.SubscriptionWrapper;
import ru.argustelecom.box.env.lifecycle.api.event.RoutedTo;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.env.task.TaskRepository;
import ru.argustelecom.box.env.task.model.TaskType;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.box.publang.billing.model.ISubscription;

@DomainService
public class SubscriptionEventListener {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TaskRepository taskRp;

	@Inject
	private SubscriptionWrapper subscriptionWr;

	void onActive(@Observes @RoutedTo(ACTIVE) ISubscription subscription) {
		taskRp.create(null, getDefaultRole(), null, subscriptionWr.unwrap(subscription),
				new Date(), TaskType.RESOURCES_ACTIVATION);
	}

	void onClosureWaiting(@Observes @RoutedTo(CLOSURE_WAITING) ISubscription subscription) {
		taskRp.create(null, getDefaultRole(), null, subscriptionWr.unwrap(subscription),
				new Date(), TaskType.RESOURCES_DEACTIVATION);
	}

	void onSuspensionForDebtWaiting(@Observes @RoutedTo(SUSPENSION_FOR_DEBT_WAITING) ISubscription subscription) {
		taskRp.create(null, getDefaultRole(), null, subscriptionWr.unwrap(subscription),
				new Date(), TaskType.RESOURCES_SUSPENSION_FOR_DEBT);
	}

	void onSuspensionOnDemandWaiting(@Observes @RoutedTo(SUSPENSION_ON_DEMAND_WAITING) ISubscription subscription) {
		taskRp.create(null, getDefaultRole(), null, subscriptionWr.unwrap(subscription),
				new Date(), TaskType.RESOURCES_SUSPENSION_ON_DEMAND);
	}

	private Role getDefaultRole() {
		return em.find(Role.class, 1L);
	}

}