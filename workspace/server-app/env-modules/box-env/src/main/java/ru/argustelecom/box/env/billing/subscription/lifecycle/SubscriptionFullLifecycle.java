package ru.argustelecom.box.env.billing.subscription.lifecycle;

import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVATION_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVE;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSED;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSURE_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.FORMALIZATION;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED_FOR_DEBT;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED_ON_DEMAND;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENSION_FOR_DEBT_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENSION_ON_DEMAND_WAITING;

import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoCancelActivationEvent;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoCancelClosureEvent;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoCloseInvoiceOnRouting;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoOpenInvoiceOnRouting;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoScheduleActivationEvent;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoScheduleClosureEvent;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoSetValidTo;
import ru.argustelecom.box.env.billing.subscription.lifecycle.condition.TestBalanceIsNotEnoughForInvoice;
import ru.argustelecom.box.env.billing.subscription.lifecycle.condition.TestValidityDateDoesNotComingYet;
import ru.argustelecom.box.env.billing.subscription.lifecycle.validator.MustHaveBalanceForInvoice;
import ru.argustelecom.box.env.billing.subscription.lifecycle.validator.MustHaveDefinedValidFrom;
import ru.argustelecom.box.env.billing.subscription.lifecycle.validator.MustHaveNoPendingTasks;
import ru.argustelecom.box.env.billing.subscription.lifecycle.validator.MustStillValid;
import ru.argustelecom.box.env.billing.subscription.lifecycle.validator.MustWarnOnDeactivation;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.nls.SubscriptionMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.NamedObject;

@LifecycleRegistrant(qualifier = "FULL")
public class SubscriptionFullLifecycle implements LifecycleFactory<SubscriptionState, Subscription> {

	@Override
	public void buildLifecycle(LifecycleBuilder<SubscriptionState, Subscription> lifecycle) {

		lifecycle.keyword(SubscriptionLifecycleQualifier.FULL);
		lifecycle.name("Жизненный цикл подписки c приостановкой");

		//@formatter:off
		
		// ****************************************************************************************************
		// Активация подписки
		
		lifecycle.route(Routes.ACTIVATE)
			.from(FORMALIZATION)
			.from(ACTIVATION_WAITING)
			.from(SUSPENDED_ON_DEMAND)
			
			.to(ACTIVATION_WAITING)
				.when(TestValidityDateDoesNotComingYet.class)
				.validate(MustHaveDefinedValidFrom.class)
				.execute(DoScheduleActivationEvent.class)
			.end()
			
			.to(SUSPENDED_FOR_DEBT)
				.when(TestBalanceIsNotEnoughForInvoice.class)
				.validate(MustHaveDefinedValidFrom.class)
				.execute(DoScheduleClosureEvent.class)
			.end()
			
			.to(ACTIVE)
				.validate(MustStillValid.class)	
				.validate(MustHaveDefinedValidFrom.class)	
				.execute(DoOpenInvoiceOnRouting.class)
				.execute(DoScheduleClosureEvent.class)
			.end()
		.end();


		// ****************************************************************************************************
		// Приостановка за неуплату
		
		lifecycle.route(Routes.SUSPEND_FOR_DEBT)
			.from(ACTIVE)
			.to(SUSPENSION_FOR_DEBT_WAITING)
				.validate(MustWarnOnDeactivation.class)
				.validate(MustHaveNoPendingTasks.class)
				.execute(DoCloseInvoiceOnRouting.class)
			.end()
		.end();
		
		lifecycle.route(Routes.COMPLETE_SUSPENSION_FOR_DEBT)
			.from(SUSPENSION_FOR_DEBT_WAITING)
			.to(SUSPENDED_FOR_DEBT)
				// do nothing
			.end()
			.controlledByUser(false)
		.end();
		
		lifecycle.route(Routes.ACTIVATE_AFTER_DEBT_SUSPENSION)
			.from(SUSPENDED_FOR_DEBT)
			.to(ACTIVE)
				.validate(MustStillValid.class)
				.validate(MustHaveBalanceForInvoice.class)
				.execute(DoOpenInvoiceOnRouting.class)
			.end()
		.end();

		
		// ****************************************************************************************************
		// Приостановка по требованию
		
		lifecycle.route(Routes.SUSPEND_ON_DEMAND)
			.from(ACTIVE)
			.to(SUSPENSION_ON_DEMAND_WAITING)
				.validate(MustWarnOnDeactivation.class)
				.validate(MustHaveNoPendingTasks.class)
				.execute(DoCloseInvoiceOnRouting.class)
			.end()
		.end();
		
		lifecycle.route(Routes.COMPLETE_SUSPENSION_ON_DEMAND)
			.from(SUSPENSION_ON_DEMAND_WAITING)
			.to(SUSPENDED_ON_DEMAND)
				// do nothing
			.end()
			.controlledByUser(false)
		.end();

		
		// ****************************************************************************************************
		// Закрытие подписки

		lifecycle.route(Routes.CLOSE_FROM_ACTIVE)
			.from(ACTIVE)
			.to(CLOSURE_WAITING)
				.validate(MustWarnOnDeactivation.class)
				.validate(MustHaveNoPendingTasks.class)
				.execute(DoCloseInvoiceOnRouting.class)
				.execute(DoSetValidTo.class)
			.end()
		.end();

		lifecycle.route(Routes.CLOSE_BEFORE_ACTIVATION)
			.from(ACTIVATION_WAITING)
			.to(CLOSED)
				.execute(DoCancelActivationEvent.class)
				.execute(DoSetValidTo.class)
			.end()
		.end();
	
		lifecycle.route(Routes.CLOSE_FROM_SUSPENSION)
			.from(SUSPENDED)	
			.from(SUSPENDED_FOR_DEBT)
			.from(SUSPENDED_ON_DEMAND)
			
			.to(CLOSURE_WAITING)
				.execute(DoSetValidTo.class)
			.end()
		.end();
		
		lifecycle.route(Routes.COMPLETE_CLOSURE)
			.from(CLOSURE_WAITING)
			.to(CLOSED)
				.execute(DoCancelClosureEvent.class)
			.end()
			.controlledByUser(false)
		.end();
		
		//@formatter:on
	}

	public enum Routes implements NamedObject {

		//@formatter:off
		// Активация подписки
		ACTIVATE,
		
		// Приостановка за неуплату
		SUSPEND_FOR_DEBT,
		COMPLETE_SUSPENSION_FOR_DEBT,
		ACTIVATE_AFTER_DEBT_SUSPENSION,
		
		// Приостановка по требованию
		SUSPEND_ON_DEMAND,
		COMPLETE_SUSPENSION_ON_DEMAND,
		
		// Закрытие подписки
		CLOSE_BEFORE_ACTIVATION,
		CLOSE_FROM_ACTIVE,
		CLOSE_FROM_SUSPENSION,
		COMPLETE_CLOSURE;
		//@formatter:on

		public String getName() {
			SubscriptionMessagesBundle messages = LocaleUtils.getMessages(SubscriptionMessagesBundle.class);
			switch (this) {
				case ACTIVATE:
				    return messages.routeActivate();
				case SUSPEND_FOR_DEBT:
				    return messages.routeSuspendForDebt();
				case COMPLETE_SUSPENSION_FOR_DEBT:
				    return messages.routeCompleteSuspensionForDebt();
				case ACTIVATE_AFTER_DEBT_SUSPENSION:
				    return messages.routeActivateAfterDebtSuspension();
				case SUSPEND_ON_DEMAND:
				    return messages.routeSuspendOnDemand();
				case COMPLETE_SUSPENSION_ON_DEMAND:
				    return messages.routeCompleteSuspensionOnDemand();
				case CLOSE_BEFORE_ACTIVATION:
				    return messages.routeCloseBeforeActivation();
				case CLOSE_FROM_ACTIVE:
				    return messages.routeCloseFromActive();
				case CLOSE_FROM_SUSPENSION:
				    return messages.routeCloseFromSuspension();
				case COMPLETE_CLOSURE:
				    return messages.routeCompleteClosure();
				default:
					throw new SystemException("Unsupported SubscriptionFullLifecycle.Route");
			}
		}

		@Override
		public String getObjectName() {
			return getName();
		}
	}
}
