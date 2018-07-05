package ru.argustelecom.box.env.stl.period.config;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;

public class CustomPeriodConfig extends AbstractPeriodConfig<CustomPeriodConfig> {

	private LocalDateTime startOfInterestLocal;
	private LocalDateTime endOfInterestLocal;

	private Date startOfInterestTemporal;
	private Date endOfInterestTemporal;

	private Money totalCost;
	private PeriodType periodType;
	private PeriodDuration accountingDuration;
	private PeriodDuration chargingDuration;

	public CustomPeriodConfig() {
		periodType = PeriodType.CALENDARIAN;
		accountingDuration = PeriodDuration.ofMonths(1);
		chargingDuration = PeriodDuration.ofDays(10);
	}

	@Override
	public LocalDateTime getStartOfInterest() {
		ZoneId zoneId = getZoneId();
		checkState(startOfInterestLocal != null || startOfInterestTemporal != null && zoneId != null);
		return startOfInterestLocal != null ? startOfInterestLocal : toLocalDateTime(startOfInterestTemporal, zoneId);
	}

	public CustomPeriodConfig setStartOfInterest(LocalDateTime startOfInterest) {
		this.startOfInterestTemporal = null;
		this.startOfInterestLocal = startOfInterest;
		return this;
	}

	public CustomPeriodConfig setStartOfInterest(Date startOfInterest) {
		this.startOfInterestTemporal = startOfInterest;
		this.startOfInterestLocal = null;
		return this;
	}

	@Override
	public LocalDateTime getEndOfInterest() {
		ZoneId zoneId = getZoneId();
		if (endOfInterestLocal != null || endOfInterestTemporal != null && zoneId != null) {
			return endOfInterestLocal != null ? endOfInterestLocal : toLocalDateTime(endOfInterestTemporal, zoneId);
		}
		return null;
	}

	public CustomPeriodConfig setEndOfInterest(LocalDateTime endOfInterest) {
		this.endOfInterestTemporal = null;
		this.endOfInterestLocal = endOfInterest;
		return this;
	}

	public CustomPeriodConfig setEndOfInterest(Date endOfInterest) {
		this.endOfInterestTemporal = endOfInterest;
		this.endOfInterestLocal = null;
		return this;
	}

	@Override
	public Money getTotalCost() {
		return totalCost;
	}

	public CustomPeriodConfig setTotalCost(Money totalCost) {
		this.totalCost = totalCost;
		return this;

	}

	@Override
	public PeriodType getPeriodType() {
		return periodType;
	}

	public CustomPeriodConfig setPeriodType(PeriodType periodType) {
		this.periodType = periodType;
		return this;
	}

	@Override
	public PeriodDuration getAccountingDuration() {
		return accountingDuration;
	}

	public CustomPeriodConfig setAccountingDuration(PeriodDuration accountingDuration) {
		this.accountingDuration = accountingDuration;
		return this;
	}

	@Override
	public PeriodDuration getChargingDuration() {
		return chargingDuration;
	}

	public CustomPeriodConfig setChargingDuration(PeriodDuration chargingDuration) {
		this.chargingDuration = chargingDuration;
		return this;
	}
}
