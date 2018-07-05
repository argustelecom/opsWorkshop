package ru.argustelecom.box.env.stl.period;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.time.LocalDateTime;

import ru.argustelecom.box.env.stl.Money;

public interface PeriodConfig {

	LocalDateTime getPoi();

	LocalDateTime getStartOfInterest();

	LocalDateTime getEndOfInterest();

	Money getTotalCost();

	PeriodType getPeriodType();

	PeriodDuration getAccountingDuration();

	PeriodDuration getChargingDuration();

	default void validate() {
		checkRequiredArgument(getPoi(), "PeriodConfig.poi");
		checkRequiredArgument(getStartOfInterest(), "PeriodConfig.startOfInterest");
		checkRequiredArgument(getTotalCost(), "PeriodConfig.totalCost");
		checkRequiredArgument(getPeriodType(), "PeriodConfig.periodType");
		checkRequiredArgument(getAccountingDuration(), "PeriodConfig.accountingDuration");
		checkRequiredArgument(getChargingDuration(), "PeriodConfig.chargingDuration");
	}
}