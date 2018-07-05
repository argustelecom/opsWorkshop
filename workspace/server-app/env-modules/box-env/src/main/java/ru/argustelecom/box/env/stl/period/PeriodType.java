package ru.argustelecom.box.env.stl.period;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.stl.nls.PeriodMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;
import ru.argustelecom.system.inf.exception.SystemException;

public enum PeriodType {

	CALENDARIAN(true, true, PeriodUnit.DAY,
			PeriodUnit.arrayOf(PeriodUnit.MONTH, PeriodUnit.QUARTER, PeriodUnit.SEMESTER, PeriodUnit.YEAR),
			PeriodUnit.arrayOf(PeriodUnit.DAY, PeriodUnit.MONTH, PeriodUnit.QUARTER, PeriodUnit.SEMESTER,
					PeriodUnit.YEAR)) {

		@Override
		public Range<LocalDateTime> calculatePeriodBoundaries(LocalDateTime soi, LocalDateTime poi,
				PeriodDuration periodDuration) {

			return periodDuration.getUnit().boundariesOf(poi);
		}

	},

	CUSTOM(false, false, PeriodUnit.HOUR,
			PeriodUnit.arrayOf(PeriodUnit.HOUR, PeriodUnit.DAY, PeriodUnit.MONTH),
			PeriodUnit.arrayOf(PeriodUnit.HOUR, PeriodUnit.DAY, PeriodUnit.MONTH)) {

		@Override
		public Range<LocalDateTime> calculatePeriodBoundaries(LocalDateTime soi, LocalDateTime poi,
				PeriodDuration periodDuration) {

			return PeriodUtils.createPeriod(soi, poi, periodDuration);
		}

	},

	DEBUG(false, false, PeriodUnit.MINUTE,
			PeriodUnit.arrayOf(PeriodUnit.HOUR, PeriodUnit.DAY, PeriodUnit.WEEK, PeriodUnit.MONTH),
			PeriodUnit.arrayOf(PeriodUnit.MINUTE, PeriodUnit.HOUR, PeriodUnit.DAY, PeriodUnit.WEEK, PeriodUnit.MONTH)) {

		@Override
		public Range<LocalDateTime> calculatePeriodBoundaries(LocalDateTime soi, LocalDateTime poi,
				PeriodDuration periodDuration) {

			return PeriodUtils.createPeriod(soi, poi, periodDuration);
		}

	};

	private static final PeriodType[] PRODUCTION_VALUES = { CALENDARIAN, CUSTOM };

	private boolean fixedAccountingDuration;
	private boolean calendarAligned;
	private PeriodUnit baseUnit;
	private PeriodUnit[] accountingPeriodUnits;
	private PeriodUnit[] chargingPeriodUnits;

	private PeriodType(boolean fixedAccountingDuration, boolean calendarAligned, PeriodUnit baseUnit,
			PeriodUnit[] accountingPeriodUnits, PeriodUnit[] chargingPeriodUnits) {

		this.fixedAccountingDuration = fixedAccountingDuration;
		this.calendarAligned = calendarAligned;
		this.baseUnit = baseUnit;
		this.accountingPeriodUnits = accountingPeriodUnits;
		this.chargingPeriodUnits = chargingPeriodUnits;
	}

	public boolean isFixedAccountingDuration() {
		return fixedAccountingDuration;
	}

	public boolean isCalendarAligned() {
		return calendarAligned;
	}

	public PeriodUnit getBaseUnit() {
		return baseUnit;
	}

	public PeriodUnit[] getAccountingPeriodUnits() {
		return accountingPeriodUnits;
	}

	public PeriodUnit[] getChargingPeriodUnits() {
		return chargingPeriodUnits;
	}

	public PeriodUnit[] getChargingPeriodUnits(PeriodUnit accountingPeriodUnit) {
		int index = 0;
		for (int i = 0; i < chargingPeriodUnits.length; i++) {
			if (chargingPeriodUnits[i].equals(accountingPeriodUnit)) {
				index = i + 1;
				break;
			}
		}
		return Arrays.copyOfRange(chargingPeriodUnits, 0, index);
	}

	public Range<LocalDateTime> calculateBaseUnitBoundaries(LocalDateTime soi, LocalDateTime poi) {
		return PeriodUtils.createBaseUnitBounds(soi, poi, this);
	}

	public abstract Range<LocalDateTime> calculatePeriodBoundaries(LocalDateTime soi, LocalDateTime poi,
			PeriodDuration periodDuration);

	public boolean isSupportedAccountingPeriodUnit(PeriodUnit unit) {
		return isSupportedUnit(accountingPeriodUnits, unit);
	}

	public boolean isSupportedChargingPeriodUnit(PeriodUnit unit) {
		return isSupportedUnit(chargingPeriodUnits, unit);
	}

	private boolean isSupportedUnit(PeriodUnit[] supportedUnits, PeriodUnit testedUnits) {
		for (PeriodUnit currentUnit : supportedUnits) {
			if (currentUnit == testedUnits) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {

		PeriodMessagesBundle messages = LocaleUtils.getMessages(PeriodMessagesBundle.class);

		switch (this) {
			case CALENDARIAN:
				return messages.periodTypeCalendarian();
			case CUSTOM:
				return messages.periodTypeCustom();
			case DEBUG:
				return messages.periodTypeDebug();
			default:
				throw new SystemException("Unsupported PeriodType");
		}
	}

	public static PeriodType[] availableValues() {
		return ServerRuntimeProperties.instance().getAppDebugMode() ? values() : PRODUCTION_VALUES;
	}
}
