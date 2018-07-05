package ru.argustelecom.box.env.billing.invoice.lifecycle;

import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.ABORTED;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.DONE;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.FORMALIZATION;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.PERFORMED_BILLING;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.PERFORMED_PRE_BILLING;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.SYNCHRONIZATION;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.util.function.Function;

import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoProcessedJob;
import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoRestoreServiceContextRechargeJob;
import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoSynchronizationRatedOutgoingCalls;
import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoWriteEmptyComment;
import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoWriteEmptyQueueComment;
import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoWriteQueueComment;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.billing.invoice.nls.ChargeJobMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * Описывает <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6718685">граф состояний заданий
 * на перетарификацию</a>
 */
@LifecycleRegistrant(qualifier = "FULL")
public class RechargingChargeJobLifecycle implements LifecycleFactory<ChargeJobState, ChargeJob> {

	@Override
	public void buildLifecycle(LifecycleBuilder<ChargeJobState, ChargeJob> lifecycle) {
		ChargeJobMessagesBundle messages = getMessages(ChargeJobMessagesBundle.class);

		lifecycle.keyword(getClass().getSimpleName());
		lifecycle.name(messages.rechargingLifecycleName());

		//@formatter:off
		lifecycle.route(Route.PERFORM_AT_PRE_BILLING)
			.from(FORMALIZATION)
			.to(PERFORMED_PRE_BILLING)
				.execute(DoWriteEmptyComment.class)
			.end()
		.end().route(Route.SYNCHRONIZE_RECHARGING)
			.from(PERFORMED_PRE_BILLING)
			.to(SYNCHRONIZATION)
				.execute(DoWriteQueueComment.class)
			.end()
		.end().route(Route.PERFORM_AT_BILLING)
			.from(SYNCHRONIZATION)
			.to(PERFORMED_BILLING)
				.execute(DoSynchronizationRatedOutgoingCalls.class)
				.execute(DoRestoreServiceContextRechargeJob.class)
				.execute(DoWriteEmptyQueueComment.class)
			.end()
		.end().route(Route.PERFORM_AT_PRE_BILLING_FROM_FORMALIZATION)
			.from(FORMALIZATION)
			.to(PERFORMED_BILLING)
				.execute(DoRestoreServiceContextRechargeJob.class)
				.execute(DoWriteEmptyComment.class)
			.end()
		.end().route(Route.FINISH)
			.from(PERFORMED_BILLING)
			.to(DONE)
				.execute(DoProcessedJob.class)
				.execute(DoWriteQueueComment.class)
			.end()
		.end().route(Route.ABORT)
			.from(PERFORMED_BILLING)
			.to(ABORTED)
				.execute(DoProcessedJob.class)
				.execute(DoWriteQueueComment.class)
			.end()
		.end().route(Route.ABORT_FROM_PRE_BILLING)
			.from(PERFORMED_PRE_BILLING)
			.to(ABORTED)
				.execute(DoProcessedJob.class)
				.execute(DoWriteQueueComment.class)
			.end()
		.end();
		//@formatter:on
	}

	public enum Route implements NamedObject {
		//@formatter:off
		PERFORM_AT_PRE_BILLING						(ChargeJobMessagesBundle::performAtPreBilling),
		PERFORM_AT_PRE_BILLING_FROM_FORMALIZATION	(ChargeJobMessagesBundle::performAtPreBilling),
		SYNCHRONIZE_CHARGING						(ChargeJobMessagesBundle::synchronizeCharging),
		SYNCHRONIZE_RECHARGING						(ChargeJobMessagesBundle::synchronizeRecharging),
		PERFORM_AT_BILLING							(ChargeJobMessagesBundle::performAtBilling),
		FINISH										(ChargeJobMessagesBundle::finish),
		ABORT										(ChargeJobMessagesBundle::abort),
		ABORT_FROM_PRE_BILLING						(ChargeJobMessagesBundle::abort);
		//@formatter:on

		private Function<ChargeJobMessagesBundle, String> nameGetter;

		Route(Function<ChargeJobMessagesBundle, String> nameGetter) {
			this.nameGetter = nameGetter;
		}

		@Override
		public String getObjectName() {
			return nameGetter.apply(getMessages(ChargeJobMessagesBundle.class));
		}
	}
}