package ru.argustelecom.box.env.billing.period;

import java.time.LocalDateTime;
import java.util.Date;

import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.period.config.InvoiceBasedConfig;
import ru.argustelecom.box.env.billing.period.config.SubscriptionBasedConfig;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.stl.period.AccountingPeriod;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;

public class PeriodBuilderService {

	public static AccountingPeriod accountingOf(Subscription subscription) {
		return accountingOf(subscription, subscription.getValidFrom());
	}

	public static AccountingPeriod accountingOf(Subscription subscription, Date poi) {
		SubscriptionBasedConfig config = new SubscriptionBasedConfig(subscription);
		config.setPoi(poi);
		return AccountingPeriod.create(config);
	}

	public static AccountingPeriod accountingOf(Subscription subscription, LocalDateTime poi) {
		SubscriptionBasedConfig config = new SubscriptionBasedConfig(subscription);
		config.setPoi(poi);
		return AccountingPeriod.create(config);
	}

	public static AccountingPeriod accountingOf(LongTermInvoice invoice) {
		InvoiceBasedConfig config = new InvoiceBasedConfig(invoice);
		return AccountingPeriod.create(config);
	}

	public static ChargingPeriod chargingOf(Subscription subscription) {
		AccountingPeriod accountingPeriod = accountingOf(subscription);
		return accountingPeriod.chargingPeriodAt(subscription.getValidFrom());
	}

	public static ChargingPeriod chargingOf(Subscription subscription, Date poi) {
		AccountingPeriod accountingPeriod = accountingOf(subscription, poi);
		return accountingPeriod.chargingPeriodAt(poi);
	}

	public static ChargingPeriod chargingOf(Subscription subscription, LocalDateTime poi) {
		AccountingPeriod accountingPeriod = accountingOf(subscription, poi);
		return accountingPeriod.chargingPeriodAt(poi);
	}

	public static ChargingPeriod chargingOf(LongTermInvoice invoice) {
		AccountingPeriod accountingPeriod = accountingOf(invoice);
		return accountingPeriod.chargingPeriodAt(invoice.getStartDate());
	}

}