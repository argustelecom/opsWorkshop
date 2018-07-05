package ru.argustelecom.box.env.billing.period.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.LocalDateTime;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.config.AbstractPeriodConfig;
import ru.argustelecom.box.inf.chrono.ChronoUtils;

public class SubscriptionBasedConfig extends AbstractPeriodConfig<SubscriptionBasedConfig> {

	private Subscription subscription;

	public SubscriptionBasedConfig(Subscription subscription) {
		this.subscription = checkNotNull(subscription);
		this.setPoi(subscription.getValidFrom());
	}

	@Override
	public LocalDateTime getStartOfInterest() {
		return ChronoUtils.toLocalDateTime(subscription.getValidFrom(), getZoneId());
	}

	@Override
	public LocalDateTime getEndOfInterest() {
		if (subscription.getValidTo() != null) {
			return ChronoUtils.toLocalDateTime(subscription.getValidTo(), getZoneId());
		}
		return null;
	}

	@Override
	public Money getTotalCost() {
		return subscription.getCost();
	}

	@Override
	public PeriodType getPeriodType() {
		return subscription.getProvisionTerms().getPeriodType();
	}

	@Override
	public PeriodDuration getAccountingDuration() {
		return subscription.getAccountingDuration();
	}

	@Override
	public PeriodDuration getChargingDuration() {
		return subscription.getProvisionTerms().getChargingDuration();
	}

}
