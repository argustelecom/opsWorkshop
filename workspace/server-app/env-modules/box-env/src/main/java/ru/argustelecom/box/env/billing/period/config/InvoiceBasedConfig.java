package ru.argustelecom.box.env.billing.period.config;

import java.time.LocalDateTime;

import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.config.AbstractPeriodConfig;

public class InvoiceBasedConfig extends AbstractPeriodConfig<InvoiceBasedConfig> {

	private SubscriptionBasedConfig delegate;

	public InvoiceBasedConfig(LongTermInvoice invoice) {
		this.delegate = new SubscriptionBasedConfig(invoice.getSubscription());
		this.setPoi(invoice.getStartDate());
	}

	@Override
	public LocalDateTime getStartOfInterest() {
		return delegate.getStartOfInterest();
	}

	@Override
	public LocalDateTime getEndOfInterest() {
		return delegate.getEndOfInterest();
	}

	@Override
	public Money getTotalCost() {
		return delegate.getTotalCost();
	}

	@Override
	public PeriodType getPeriodType() {
		return delegate.getPeriodType();
	}

	@Override
	public PeriodDuration getAccountingDuration() {
		return delegate.getAccountingDuration();
	}

	@Override
	public PeriodDuration getChargingDuration() {
		return delegate.getChargingDuration();
	}
}