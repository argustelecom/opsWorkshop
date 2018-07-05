package ru.argustelecom.box.env.billing.subscription.lifecycle;

import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVATION_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVE;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSED;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSURE_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.FORMALIZATION;

import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoCancelActivationEvent;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoCancelClosureEvent;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoCloseInvoiceOnRouting;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoOpenInvoiceOnRouting;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoScheduleActivationEvent;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoScheduleClosureEvent;
import ru.argustelecom.box.env.billing.subscription.lifecycle.action.DoSetValidTo;
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

@LifecycleRegistrant(qualifier = "SHORT")
public class SubscriptionShortLifecycle implements LifecycleFactory<SubscriptionState, Subscription> {

	@Override
	public void buildLifecycle(LifecycleBuilder<SubscriptionState, Subscription> lifecycle) {

		lifecycle.keyword(SubscriptionLifecycleQualifier.SHORT);
		lifecycle.name("Жизненный цикл подписки без приостановки");

		//@formatter:off
		lifecycle.route(Routes.ACTIVATE)
			.from(FORMALIZATION)
			.from(ACTIVATION_WAITING)
			
			.to(ACTIVATION_WAITING)
				.when(TestValidityDateDoesNotComingYet.class)
				.validate(MustHaveDefinedValidFrom.class)
				.execute(DoScheduleActivationEvent.class)
			.end()
			
			.to(ACTIVE)
				.validate(MustStillValid.class)
				.validate(MustHaveDefinedValidFrom.class)
				.validate(MustHaveBalanceForInvoice.class)
				.execute(DoOpenInvoiceOnRouting.class)
				.execute(DoScheduleClosureEvent.class)
			.end()
		.end();
		
		lifecycle.route(Routes.CLOSE_BEFORE_ACTIVATION)
			.from(ACTIVATION_WAITING)
			.to(CLOSED)
				.execute(DoCancelActivationEvent.class)
			.end()
		.end();

		lifecycle.route(Routes.CLOSE)
			.from(ACTIVE)
			.to(CLOSURE_WAITING)
				.validate(MustWarnOnDeactivation.class)
				.validate(MustHaveNoPendingTasks.class)
				.execute(DoCloseInvoiceOnRouting.class)
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
		ACTIVATE,
		CLOSE,
		CLOSE_BEFORE_ACTIVATION,
		COMPLETE_CLOSURE;
		//@formatter:on

		public String getName() {
			SubscriptionMessagesBundle messages = LocaleUtils.getMessages(SubscriptionMessagesBundle.class);
			switch (this) {
				case ACTIVATE:
					return messages.routeActivate();
				case CLOSE:
					return messages.routeClose();
				case CLOSE_BEFORE_ACTIVATION:
					return messages.routeCloseBeforeActivation();
				case COMPLETE_CLOSURE:
					return messages.routeCompleteClosure();
				default:
					throw new SystemException("Unsupported SubscriptionShortLifecycle.Route");
			}
		}

		@Override
		public String getObjectName() {
			return getName();
		}
	}
}
