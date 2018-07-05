package ru.argustelecom.box.env.stl.period;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.WeekFields;
import java.util.Locale;

import ru.argustelecom.box.env.stl.nls.PeriodMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

import com.google.common.collect.Range;

public enum PeriodUnit {

	SECOND(ChronoUnit.SECONDS, 1, false) {
		@Override
		public TemporalAmount amountOf(int amount) {
			return Duration.ofSeconds(amount);
		}

		@Override
		public Range<LocalDateTime> boundariesOf(LocalDateTime poi) {
	//@formatter:off
			LocalDateTime start = LocalDateTime.of(
					poi.getYear(), poi.getMonth(), poi.getDayOfMonth(),
					poi.getHour(), poi.getMinute(), poi.getSecond(), 0
			);
			LocalDateTime end = LocalDateTime.of(
					poi.getYear(), poi.getMonth(), poi.getDayOfMonth(),
					poi.getHour(), poi.getMinute(), poi.getSecond(), 999_000_000
			);
			//@formatter:on

			return Range.closed(start, end);
		}
	},

	MINUTE(ChronoUnit.MINUTES, 1, false) {

		@Override
		public TemporalAmount amountOf(int amount) {
			return Duration.ofMinutes(amount);
		}

		@Override
		public Range<LocalDateTime> boundariesOf(LocalDateTime poi) {
	//@formatter:off
			LocalDateTime start = LocalDateTime.of(
				poi.getYear(), poi.getMonth(), poi.getDayOfMonth(), 
				poi.getHour(), poi.getMinute(), 0, 0
			);
			LocalDateTime end = LocalDateTime.of(
				poi.getYear(), poi.getMonth(), poi.getDayOfMonth(), 
				poi.getHour(), poi.getMinute(), 59, 999_000_000
			);
			//@formatter:on

			return Range.closed(start, end);
		}

	},

	HOUR(ChronoUnit.HOURS, 1, false) {

		@Override
		public TemporalAmount amountOf(int amount) {
			return Duration.ofHours(amount);
		}

		@Override
		public Range<LocalDateTime> boundariesOf(LocalDateTime poi) {
	//@formatter:off
			LocalDateTime start = LocalDateTime.of(
				poi.getYear(), poi.getMonth(), poi.getDayOfMonth(), 
				poi.getHour(), 0, 0, 0
			);
			LocalDateTime end = LocalDateTime.of(
				poi.getYear(), poi.getMonth(), poi.getDayOfMonth(), 
				poi.getHour(), 59, 59, 999_000_000
			);
			//@formatter:on

			return Range.closed(start, end);
		}

	},

	DAY(ChronoUnit.DAYS, 1, false) {

		@Override
		public TemporalAmount amountOf(int amount) {
			return Period.ofDays(amount);
		}

		@Override
		public Range<LocalDateTime> boundariesOf(LocalDateTime poi) {
	//@formatter:off
			LocalDateTime start = LocalDateTime.of(
				poi.getYear(), poi.getMonth(), poi.getDayOfMonth(), 
				0, 0, 0, 0
			);
			LocalDateTime end = LocalDateTime.of(
				poi.getYear(), poi.getMonth(), poi.getDayOfMonth(), 
				23, 59, 59, 999_000_000
			);
			//@formatter:on

			return Range.closed(start, end);
		}

	},

	WEEK(ChronoUnit.WEEKS, 1, true) {

		@Override
		public TemporalAmount amountOf(int amount) {
			return Period.ofWeeks(amount);
		}

		@Override
		public Range<LocalDateTime> boundariesOf(LocalDateTime poi) {
	//@formatter:off
			LocalDateTime weekStart = moveToStartOfWeek(poi);
			LocalDateTime weekEnd = weekStart.plusDays(6);
			
			LocalDateTime start = LocalDateTime.of(
				weekStart.getYear(), weekStart.getMonth(), weekStart.getDayOfMonth(), 
				0, 0, 0, 0
			);
			LocalDateTime end = LocalDateTime.of(
				weekEnd.getYear(), weekEnd.getMonth(), weekEnd.getDayOfMonth(), 
				23, 59, 59, 999_000_000
			);
			//@formatter:on

			return Range.closed(start, end);
		}

		private LocalDateTime moveToStartOfWeek(LocalDateTime poi) {
			// TODO Наверное, нужно вынести в явную настройку, а не определять по дефолтной локали
			DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
			LocalDateTime result = poi.with(firstDayOfWeek);
			if (firstDayOfWeek.compareTo(poi.getDayOfWeek()) > 0) {
				result = result.minusWeeks(1);
			}
			return result;
		}

	},

	MONTH(ChronoUnit.MONTHS, 1, true) {

		@Override
		public TemporalAmount amountOf(int amount) {
			return Period.ofMonths(amount);
		}

		@Override
		public Range<LocalDateTime> boundariesOf(LocalDateTime poi) {
	//@formatter:off
			boolean leapYear = LocalDate.from(poi).isLeapYear();
			
			LocalDateTime start = LocalDateTime.of(
				poi.getYear(), poi.getMonth(), 1, 
				0, 0, 0, 0
			);
			LocalDateTime end = LocalDateTime.of(
				poi.getYear(), poi.getMonth(), poi.getMonth().length(leapYear), 
				23, 59, 59, 999_000_000
			);
			//@formatter:on

			return Range.closed(start, end);
		}

	},

	QUARTER(ChronoUnit.MONTHS, 3, true) {

		@Override
		public TemporalAmount amountOf(int amount) {
			return Period.ofMonths(amount * 3);
		}

		@Override
		public Range<LocalDateTime> boundariesOf(LocalDateTime poi) {
	//@formatter:off
			int quarterStartMonth = (((poi.getMonthValue() - 1) / 3) * 3) + 1;
			int quarterEndMoth = quarterStartMonth + 2;
			
			LocalDateTime start = LocalDateTime.of(
				poi.getYear(), Month.of(quarterStartMonth), 1, 
				0, 0, 0, 0
			);
			LocalDateTime end = LocalDateTime.of(
				poi.getYear(), Month.of(quarterEndMoth), Month.of(quarterEndMoth).length(false), 
				23, 59, 59, 999_000_000
			);
			//@formatter:on

			return Range.closed(start, end);
		}

	},

	SEMESTER(ChronoUnit.MONTHS, 6, true) {

		@Override
		public TemporalAmount amountOf(int amount) {
			return Period.ofMonths(amount * 6);
		}

		@Override
		public Range<LocalDateTime> boundariesOf(LocalDateTime poi) {
	//@formatter:off
			Month semesterStartMonth = poi.getMonthValue() <= 6 ? Month.JANUARY : Month.JULY;
			Month semesterEndMoth = poi.getMonthValue() <= 6 ? Month.JUNE : Month.DECEMBER;
			
			LocalDateTime start = LocalDateTime.of(
				poi.getYear(), semesterStartMonth, 1, 
				0, 0, 0, 0
			);
			LocalDateTime end = LocalDateTime.of(
				poi.getYear(), semesterEndMoth, semesterEndMoth.length(false), 
				23, 59, 59, 999_000_000
			);
			//@formatter:on

			return Range.closed(start, end);
		}

	},

	YEAR(ChronoUnit.YEARS, 1, true) {

		@Override
		public TemporalAmount amountOf(int amount) {
			return Period.ofYears(amount);
		}

		@Override
		public Range<LocalDateTime> boundariesOf(LocalDateTime poi) {
	//@formatter:off
			LocalDateTime start = LocalDateTime.of(
				poi.getYear(), Month.JANUARY, 1, 
				0, 0, 0, 0
			);
			LocalDateTime end = LocalDateTime.of(
				poi.getYear(), Month.DECEMBER, Month.DECEMBER.length(false), 
				23, 59, 59, 999_000_000
			);
			//@formatter:on

			return Range.closed(start, end);
		}

	};

	private ChronoUnit chronoUnit;
	private int multiplicand;
	private Duration duration;
	private boolean estimated;

	private PeriodUnit(ChronoUnit chronoUnit, int multiplicand, boolean estimated) {
		this.chronoUnit = chronoUnit;
		this.estimated = estimated;
		this.multiplicand = multiplicand;
		this.duration = chronoUnit.getDuration().multipliedBy(multiplicand);
	}

	public boolean isEstimatedDuration() {
		return estimated;
	}

	public ChronoUnit getChronoUnit() {
		return chronoUnit;
	}

	public Duration getDuration() {
		return duration;
	}

	public abstract TemporalAmount amountOf(int amount);

	public Duration durationOf(int amount) {
		return duration.multipliedBy(amount);
	}

	public abstract Range<LocalDateTime> boundariesOf(LocalDateTime poi);

	public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
		return chronoUnit.between(temporal1Inclusive, temporal2Exclusive) / multiplicand;
	}

	@SuppressWarnings("unchecked")
	public <T extends Temporal> T addTo(T temporal, int amountToAdd) {
		return (T) temporal.plus((long) amountToAdd * multiplicand, chronoUnit);
	}

	@SuppressWarnings("unchecked")
	public <T extends Temporal> T subtractFrom(T temporal, int amountToSubtract) {
		return (T) temporal.minus((long) amountToSubtract * multiplicand, chronoUnit);
	}

	public boolean greater(PeriodUnit that) {
		return this.compareTo(that) > 0;
	}

	public boolean greaterOrEquals(PeriodUnit that) {
		return this.compareTo(that) >= 0;
	}

	public boolean less(PeriodUnit that) {
		return this.compareTo(that) < 0;
	}

	public boolean lessOrEquals(PeriodUnit that) {
		return this.compareTo(that) <= 0;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {

		PeriodMessagesBundle messages = LocaleUtils.getMessages(PeriodMessagesBundle.class);

		switch (this) {
			case SECOND:
				return messages.periodUnitSecond();
			case MINUTE:
				return messages.periodUnitMinute();
			case HOUR:
				return messages.periodUnitHour();
			case DAY:
				return messages.periodUnitDay();
			case WEEK:
				return messages.periodUnitWeek();
			case MONTH:
				return messages.periodUnitMonth();
			case QUARTER:
				return messages.periodUnitQuarter();
			case SEMESTER:
				return messages.periodUnitSemester();
			case YEAR:
				return messages.periodUnitYear();
			default:
				throw new SystemException("Unsupported PeriodUnit");
		}
	}

	public static PeriodUnit[] arrayOf(PeriodUnit... periodUnits) {
		return periodUnits;
	}
}