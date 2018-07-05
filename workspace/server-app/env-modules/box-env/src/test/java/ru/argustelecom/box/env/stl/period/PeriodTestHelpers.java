package ru.argustelecom.box.env.stl.period;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.config.CustomPeriodConfig;
import ru.argustelecom.box.inf.chrono.ChronoUtils;

public class PeriodTestHelpers {

	public static final String DEFAULT_COST = "300";
	public static final String DEFAULT_SOI = "1970-01-01 00:00:00.000";
	
	public static final PeriodDuration DEFAULT_CHARGING_DURATION = PeriodDuration.ofDays(3);
	public static final PeriodDuration DEFAULT_ACCOUNTING_DURATION = PeriodDuration.ofMonths(1);
	
	public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private PeriodTestHelpers() {
		// should be never instantiated
	}

	public static LocalDateTime strToLdt(String strLdt) {
		return LocalDateTime.parse(strLdt, DEFAULT_FORMATTER);
	}

	public static Date strToDate(String strLdt) {
		return ChronoUtils.fromLocalDateTime(strToLdt(strLdt));
	}

	public static Date strToDate(String strLdt, ZoneId zoneId) {
		return ChronoUtils.fromLocalDateTime(strToLdt(strLdt), zoneId);
	}

	public static String ldtToStr(LocalDateTime ldt) {
		return DEFAULT_FORMATTER.format(ldt);
	}

	public static String dateToStr(Date date) {
		return ldtToStr(ChronoUtils.toLocalDateTime(date));
	}

	public static String dateToStr(Date date, ZoneId zoneId) {
		return ldtToStr(ChronoUtils.toLocalDateTime(date, zoneId));
	}

	public static ChargingPeriod chargingOf(PeriodType periodType, String poi) {
		return chargingOf(periodType, DEFAULT_SOI, poi, DEFAULT_COST, DEFAULT_ACCOUNTING_DURATION,
				DEFAULT_CHARGING_DURATION);
	}

	public static ChargingPeriod chargingOf(PeriodType periodType, String soi, String poi, String cost,
			PeriodDuration accountingDuration, PeriodDuration chargingDuration) {

		return accountingOf(periodType, soi, poi, cost, accountingDuration, chargingDuration)
				.chargingPeriodAt(strToLdt(poi));
	}

	public static AccountingPeriod accountingOf(PeriodType periodType, String poi) {
		return accountingOf(periodType, DEFAULT_SOI, poi, DEFAULT_COST, DEFAULT_ACCOUNTING_DURATION,
				DEFAULT_CHARGING_DURATION);
	}

	public static AccountingPeriod accountingOf(PeriodType periodType, String soi, String poi, String cost,
			PeriodDuration accountingDuration, PeriodDuration chargingDuration) {

		CustomPeriodConfig config = new CustomPeriodConfig();
		config.setPeriodType(periodType);
		config.setStartOfInterest(strToLdt(soi));
		config.setPoi(strToLdt(poi));
		config.setTotalCost(new Money(cost));
		config.setAccountingDuration(accountingDuration);
		config.setChargingDuration(chargingDuration);

		return AccountingPeriod.create(config);
	}
}
