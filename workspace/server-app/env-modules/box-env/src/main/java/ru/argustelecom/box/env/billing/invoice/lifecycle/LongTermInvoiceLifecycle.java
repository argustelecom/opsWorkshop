package ru.argustelecom.box.env.billing.invoice.lifecycle;

import static ru.argustelecom.box.env.billing.invoice.lifecycle.LongTermInvoiceLifecycle.Route.ACTIVATE;
import static ru.argustelecom.box.env.billing.invoice.lifecycle.LongTermInvoiceLifecycle.Route.CANCEL;
import static ru.argustelecom.box.env.billing.invoice.lifecycle.LongTermInvoiceLifecycle.Route.CLOSE;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.ACTIVE;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.CANCELLED;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.CLOSED;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.CREATED;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.util.function.Function;

import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoActivateInvoice;
import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoCancelInvoice;
import ru.argustelecom.box.env.billing.invoice.lifecycle.action.DoCloseInvoice;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.invoice.nls.InvoiceMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;

@LifecycleRegistrant
public class LongTermInvoiceLifecycle implements LifecycleFactory<InvoiceState, LongTermInvoice> {

	@Override
	public void buildLifecycle(LifecycleBuilder<InvoiceState, LongTermInvoice> lifecycle) {

		lifecycle.keyword(getClass().getSimpleName());
		lifecycle.name("Жизненный цикл инвойса");

		// @formatter:off
		
		lifecycle.route(ACTIVATE, ACTIVATE.getName())
			.from(CREATED)
			.to(ACTIVE)
				.execute(DoActivateInvoice.class)
			.end()
		.end();

		lifecycle.route(CANCEL, CANCEL.getName())
			.from(CREATED)
			.from(ACTIVE)
			.to(CANCELLED)
				.execute(DoCancelInvoice.class)
			.end()
		.end();

		lifecycle.route(CLOSE, CLOSE.getName())
			.from(ACTIVE)
			.to(CLOSED)
				.execute(DoCloseInvoice.class)
			.end()
		.end();
		//@formatter:on
	}

	protected enum Route {
		//@formatter:off
		ACTIVATE(InvoiceMessagesBundle::routeActivate),
		CLOSE(InvoiceMessagesBundle::routeCancel),
		CANCEL(InvoiceMessagesBundle::routeClose);
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
