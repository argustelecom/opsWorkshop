package ru.argustelecom.box.env.billing.subscription.lifecycle;

import static java.util.Arrays.asList;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVATION_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVE;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSURE_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.FORMALIZATION;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED_FOR_DEBT;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED_ON_DEMAND;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENSION_FOR_DEBT_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENSION_ON_DEMAND_WAITING;

import java.io.Serializable;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.validation.ValidationIssue;
import ru.argustelecom.system.inf.validation.ValidationResult;

/**
 * Временный сервис для программного управления ЖЦ подписки. Умрет после того, как в ЖЦ будет возвращен forcedRouting
 */
@DomainService
public class SubscriptionRoutingService implements Serializable {

	private static final long serialVersionUID = -5954677280402094986L;

	private static final SubscriptionWarningSuppressor WARN_SUPPRESSOR = new SubscriptionWarningSuppressor();

	@Inject
	private LifecycleRoutingService routings;

	public boolean activate(Subscription subscription) {
		if (subscription.inState(asList(FORMALIZATION, ACTIVATION_WAITING, SUSPENDED_ON_DEMAND, SUSPENDED_FOR_DEBT))) {
			routings.performRouting(subscription, SubscriptionState.ACTIVE, false, WARN_SUPPRESSOR);
			return true;
		}
		return false;
	}

	public boolean suspendOnDemand(Subscription subscription) {
		if (subscription.getLifecycleQualifier() != SubscriptionLifecycleQualifier.FULL) {
			return false;
		}

		if (subscription.inState(ACTIVE)) {
			routings.performRouting(subscription, SubscriptionState.SUSPENSION_ON_DEMAND_WAITING, true, WARN_SUPPRESSOR);
			return true;
		}
		return false;
	}

	public boolean completeSuspensionOnDemand(Subscription subscription) {
		if (subscription.getLifecycleQualifier() != SubscriptionLifecycleQualifier.FULL) {
			return false;
		}

		if (subscription.inState(SUSPENSION_ON_DEMAND_WAITING)) {
			routings.performRouting(subscription, SubscriptionState.SUSPENDED_ON_DEMAND, true, WARN_SUPPRESSOR);
			return true;
		}
		return false;
	}

	public boolean suspendForDebt(Subscription subscription) {
		if (subscription.getLifecycleQualifier() != SubscriptionLifecycleQualifier.FULL) {
			return false;
		}

		if (subscription.inState(ACTIVE)) {
			routings.performRouting(subscription, SubscriptionState.SUSPENSION_FOR_DEBT_WAITING, true, WARN_SUPPRESSOR);
			return true;
		}
		return false;
	}

	public boolean completeSuspensionForDebt(Subscription subscription) {
		if (subscription.getLifecycleQualifier() != SubscriptionLifecycleQualifier.FULL) {
			return false;
		}

		if (subscription.inState(SUSPENSION_FOR_DEBT_WAITING)) {
			routings.performRouting(subscription, SubscriptionState.SUSPENDED_FOR_DEBT, true, WARN_SUPPRESSOR);
			return true;
		}
		return false;
	}

	public boolean close(Subscription subscription) {
		Subscription subs = subscription;
		if (subs.inState(asList(SUSPENDED, SUSPENDED_ON_DEMAND, SUSPENDED_FOR_DEBT, ACTIVE))) {
			routings.performRouting(subscription, SubscriptionState.CLOSURE_WAITING, true, WARN_SUPPRESSOR);
			return true;
		}
		if (subs.inState(ACTIVATION_WAITING)) {
			routings.performRouting(subscription, SubscriptionState.CLOSED, true, WARN_SUPPRESSOR);
			return true;
		}

		return false;
	}

	public boolean completeClosure(Subscription subscription) {
		if (subscription.inState(CLOSURE_WAITING)) {
			routings.performRouting(subscription, SubscriptionState.CLOSED, true, WARN_SUPPRESSOR);
			return true;
		}
		return false;
	}

	private static class SubscriptionWarningSuppressor extends LifecyclePhaseListener<SubscriptionState, Subscription> {
		@Override
		public void beforeRouteExecution(ExecutionCtx<SubscriptionState, ? extends Subscription> ctx,
				ValidationResult<Object> result) {

			if (result.hasWarnings() && !result.hasErrors()) {
				ctx.suppressWarnings();
				for (ValidationIssue<Object> warning : result.getWarnings()) {
					log.infov("Warning of subject {0} was suppressed: {1}", warning.getSource(), warning.getMessage());
				}
			}

		}

		private static final Logger log = Logger.getLogger(SubscriptionWarningSuppressor.class);
	}
}
