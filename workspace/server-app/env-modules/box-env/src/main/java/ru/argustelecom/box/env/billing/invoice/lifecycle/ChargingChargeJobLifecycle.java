package ru.argustelecom.box.env.billing.invoice.lifecycle;

import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.SYNCHRONIZATION;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.SYNCHRONIZED;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.util.function.Function;

import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoProcessedJob;
import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoSynchronizationRatedOutgoingCalls;
import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoWriteEmptyQueueComment;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.billing.invoice.nls.ChargeJobMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * Описывает <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6718685">граф состояний заданий
 * на тарификацию</a>
 */
@LifecycleRegistrant(qualifier = "SHORT")
public class ChargingChargeJobLifecycle implements LifecycleFactory<ChargeJobState, ChargeJob> {

	@Override
	public void buildLifecycle(LifecycleBuilder<ChargeJobState, ChargeJob> lifecycle) {
		ChargeJobMessagesBundle messages = getMessages(ChargeJobMessagesBundle.class);

		lifecycle.keyword(getClass().getSimpleName());
		lifecycle.name(messages.chargingLifecycleName());

		// @formatter:off
		lifecycle.route(Route.SYNCHRONIZE_CHARGING)
			.from(SYNCHRONIZATION)
			.to(SYNCHRONIZED)
				.execute(DoSynchronizationRatedOutgoingCalls.class)
				.execute(DoProcessedJob.class)
				.execute(DoWriteEmptyQueueComment.class)
			.end()
		.end();
		// @formatter:on
	}

	public enum Route implements NamedObject {
		//@formatter:off
		SYNCHRONIZE_CHARGING(ChargeJobMessagesBundle::synchronizeCharging);
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