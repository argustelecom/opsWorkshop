package ru.argustelecom.box.env.billing.invoice.lifecycle;

import static ru.argustelecom.box.env.billing.invoice.lifecycle.UsageInvoiceLifecycle.Route.CANCEL;
import static ru.argustelecom.box.env.billing.invoice.lifecycle.UsageInvoiceLifecycle.Route.CLOSE;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.ACTIVE;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.CANCELLED;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.CLOSED;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoCloseUsageInvoice;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.billing.invoice.nls.InvoiceMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;

import java.util.function.Function;

@LifecycleRegistrant
public class UsageInvoiceLifecycle implements LifecycleFactory<InvoiceState, UsageInvoice> {

	@Override
	public void buildLifecycle(LifecycleBuilder<InvoiceState, UsageInvoice> lifecycle) {

		lifecycle.keyword(getClass().getSimpleName());
		lifecycle.name(getMessages(InvoiceMessagesBundle.class).usageInvoiceLifecycle());

		// @formatter:off

		lifecycle.route(CLOSE, CLOSE.getName())
			.from(ACTIVE)
			.to(CLOSED)
				.execute(DoCloseUsageInvoice.class)
			.end()
		.end();

		lifecycle.route(CANCEL, CANCEL.getName())
			.from(CLOSED)
				.to(CANCELLED)
			.end()
		.end();
		//@formatter:on
	}

	public enum Route {
		//@formatter:off
		CLOSE(InvoiceMessagesBundle::routeClose),
		CANCEL(InvoiceMessagesBundle::routeCancel);
		//@formatter:on

		private Function<InvoiceMessagesBundle, String> nameGetter;

		Route(Function<InvoiceMessagesBundle, String> nameGetter) {
			this.nameGetter = nameGetter;
		}

		public String getName() {
			return nameGetter.apply(getMessages(InvoiceMessagesBundle.class));
		}
	}
}
