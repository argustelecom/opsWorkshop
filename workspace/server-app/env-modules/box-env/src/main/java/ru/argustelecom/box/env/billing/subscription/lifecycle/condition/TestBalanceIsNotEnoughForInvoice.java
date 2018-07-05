package ru.argustelecom.box.env.billing.subscription.lifecycle.condition;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.asList;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVATION_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.FORMALIZATION;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED_ON_DEMAND;

import java.util.Date;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResolution;
import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResult;
import ru.argustelecom.box.env.billing.subscription.SubscriptionProcessingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiCondition;
import ru.argustelecom.box.env.lifecycle.api.context.TestingCtx;

@LifecycleBean
public class TestBalanceIsNotEnoughForInvoice implements LifecycleCdiCondition<SubscriptionState, Subscription> {

	@Inject
	private SubscriptionProcessingService processingSvc;

	@Override
	public boolean test(TestingCtx<SubscriptionState, ? extends Subscription> ctx) {
		Subscription subscription = ctx.getBusinessObject();
		Date executionDate = ctx.getExecutionDate();

		// Этот кондишн используется только для проверки условия при активации. Для этого пришлось использовать здесь
		// литеральное состояние, что потенциально может привести к проблемам, если вдруг кто-то решит использовать
		// это условие для какого либо другого перехода. Поэтому здесь мы просто отвалимся, что подстегнет нас исправить
		// ошибку при неаккуратном изменении жизненного цикла
		checkState(subscription.inState(asList(FORMALIZATION, ACTIVATION_WAITING, SUSPENDED_ON_DEMAND)));
		SubscriptionState toState = SubscriptionState.ACTIVE;

		// Проверка баланса отсюда приведет к блокировке лицевого счета для изменения другими транзакциями (например,
		// при списании. При начислении пущай начисляется) Это нужно для того, чтобы гарантировать, что при успешном
		// выполнении проверки баланса мы можем спокойно открывать инвойс с резервированием средств и не уйдем в минуса.
		// При этом, если резервирование не требуется (вдруг), то лицевой счет не заблокируется
		BalanceCheckingResult bcr = processingSvc.checkBalanceOnRouting(subscription, toState, executionDate, true);
		checkState(bcr != null);

		return bcr.getResolution() == BalanceCheckingResolution.DISALLOWED;
	}
}
