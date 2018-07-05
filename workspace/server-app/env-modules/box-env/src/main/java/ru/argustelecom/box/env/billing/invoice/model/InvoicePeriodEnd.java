package ru.argustelecom.box.env.billing.invoice.model;

import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.util.function.Function;

import ru.argustelecom.box.env.billing.invoice.nls.InvoiceMessagesBundle;

public enum InvoicePeriodEnd {
	//@formatter:off
	ACCOUNTING_PERIOD_END(InvoiceMessagesBundle::accountingPeriodEnd),
	CHARGING_PERIOD_END(InvoiceMessagesBundle::chargingPeriodEnd);
	//@formatter:on

	private Function<InvoiceMessagesBundle, String> nameGetter;

	InvoicePeriodEnd(Function<InvoiceMessagesBundle, String> nameGetter) {
		this.nameGetter = nameGetter;
	}

	public String getName() {
		return nameGetter.apply(getMessages(InvoiceMessagesBundle.class));
	}

	@Override
	public String toString() {
		return getName();
	}
}
