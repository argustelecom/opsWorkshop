package ru.argustelecom.box.env.billing.subscription.queue;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import javax.persistence.EntityManager;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.api.context.EntityReference;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;

public class SubscriptionRenewingContext extends Context {

	private static final long serialVersionUID = -2039248924982969096L;

	private EntityReference<PersonalAccount> accountRef;

	protected SubscriptionRenewingContext(QueueEventImpl event) {
		super(event);
	}

	public SubscriptionRenewingContext(PersonalAccount account) {
		checkRequiredArgument(account, "personalAccount");
		accountRef = new EntityReference<>(account);
	}

	public PersonalAccount getPersonalAccount(EntityManager em) {
		return accountRef.get(em);
	}
}
