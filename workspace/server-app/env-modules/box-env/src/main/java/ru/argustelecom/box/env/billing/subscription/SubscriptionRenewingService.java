package ru.argustelecom.box.env.billing.subscription;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import lombok.val;
import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService;
import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResolution;
import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResult;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.SubscriptionAccountingService;
import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionRoutingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.inf.service.DomainService;

@DomainService
public class SubscriptionRenewingService {

	@Inject
	private SubscriptionRepository subscriptionRp;

	@Inject
	private PersonalAccountBalanceService balanceSvc;

	@Inject
	private SubscriptionAccountingService accountingSvc;

	@Inject
	private SubscriptionRoutingService routingSvc;

	public void tryRenewAllOnDebtSuspension(PersonalAccount account) {
		val available = balanceSvc.getAvailableBalance(account);
		if (available.isNonNegative()) {
			val subscriptions = subscriptionRp.findSuspendedForDebtSubscriptions(account, false, 0);
			if (!subscriptions.isEmpty()) {
				tryRenew(subscriptions);
			}
		} else {
			log.infov("Баланс {0} отрицательный: {1}. Автоматическое возобновление невозможно.", account, available);
		}
	}

	private void tryRenew(List<Subscription> subscriptions) {
		val renewalDate = new Date();
		Function<Subscription, InvoicePlan> planningFunc = s -> accountingSvc.calculateNextAccruals(s, renewalDate);
		val balanceCheckingResults = balanceSvc.checkBalance(subscriptions, planningFunc, true);

		if (canActivateAll(balanceCheckingResults)) {
			balanceCheckingResults.keySet().forEach(routingSvc::activate);
		}
	}

	private boolean canActivateAll(Map<Subscription, BalanceCheckingResult> balanceCheckingResults) {
		for (val entry : balanceCheckingResults.entrySet()) {
			val subscription = entry.getKey();
			val balanceResult = entry.getValue();

			log.debugv("Проверка {0}: Доступные средства после активации {1}, результат проверки баланса {2}",
					subscription, balanceResult.getAvailable(), balanceResult.getResolution());

			if (balanceResult.getResolution() == BalanceCheckingResolution.DISALLOWED) {
				log.infov("Условие автоматической активации {0} не выполнено. Операция невозможна.", subscription);
				return false;
			}
		}
		return true;
	}

	private static final Logger log = Logger.getLogger(SubscriptionRenewingService.class);
}
