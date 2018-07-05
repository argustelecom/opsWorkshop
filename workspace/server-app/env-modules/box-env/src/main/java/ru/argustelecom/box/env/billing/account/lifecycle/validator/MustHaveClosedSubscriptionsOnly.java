package ru.argustelecom.box.env.billing.account.lifecycle.validator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.val;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.account.model.PersonalAccountState;
import ru.argustelecom.box.env.billing.subscription.model.Subscription.SubscriptionQuery;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveClosedSubscriptionsOnly implements LifecycleCdiValidator<PersonalAccountState, PersonalAccount> {

	private static final String MESSAGE = "Невозможно закрыть лицевой счет: есть незакрытые подписки ({0})";

	@PersistenceContext
	private EntityManager em;

	@Override
	public void validate(ExecutionCtx<PersonalAccountState, ? extends PersonalAccount> ctx,
			ValidationResult<Object> result) {

		val personalAccount = ctx.getBusinessObject();

		//@formatter:off
		SubscriptionQuery query = new SubscriptionQuery();
		query.and(
			query.personalAccount().equal(personalAccount),
			query.state().notEqual(SubscriptionState.CLOSED)
		);
		//@formatter:off
		
		val notClosedSubsCount = query.calcRowsCount(em);
		if (notClosedSubsCount > 0) {
			result.errorv(personalAccount, MESSAGE, notClosedSubsCount);
		}
	}
}
