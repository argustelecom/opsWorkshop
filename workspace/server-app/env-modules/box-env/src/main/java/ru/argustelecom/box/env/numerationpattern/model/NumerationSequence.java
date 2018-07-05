package ru.argustelecom.box.env.numerationpattern.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.numerationpattern.nls.NumerationMessagesBundle;
import ru.argustelecom.box.inf.chrono.ChronoUtils;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryNumericFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;
import ru.argustelecom.system.inf.exception.SystemException;

@Entity
@Access(AccessType.FIELD)
@Getter
@Setter
public class NumerationSequence extends BusinessObject {

	@Column(unique = true)
	private String name;

	private Long initialValue;

	private Long increment;

	@Column(length = 16)
	private Integer capacity;

	@Enumerated(EnumType.STRING)
	@Column(length = 7)
	private PeriodType period;

	@Temporal(TemporalType.DATE)
	private Date validTo;

	private Long currentValue;

	protected NumerationSequence() {
	}

	public NumerationSequence(Long id) {
		super(id);
	}

	public void edit(Long initialValue, Long increment, Integer capacity, PeriodType period) {

		if (this.currentValue != null) {
			if (!increment.equals(this.increment) && this.currentValue > initialValue) {
				this.currentValue = increment * (this.currentValue / increment + 1);
			}
			if (initialValue.compareTo(this.currentValue) > 0) {
				this.initialValue = initialValue;
				this.currentValue = null;
			}
		}

		this.initialValue = initialValue;
		this.validTo = this.period != period ? period.currentValidToDate(new Date()) : this.validTo;
		this.period = period;
		this.capacity = capacity;
		this.increment = increment;
	}

	public Long next() {
		if (currentValue == null) {
			currentValue = initialValue;
		} else if (validTo == null || validTo.compareTo(new Date()) >= 0) {
			currentValue += increment;
		} else {
			validTo = period.nextValidToDate(validTo);
			currentValue = initialValue;
		}
		return currentValue;
	}

	public static class NumerationSequenceQuery extends EntityQuery<NumerationSequence> {

		private EntityQueryStringFilter<NumerationSequence> name;
		private EntityQuerySimpleFilter<NumerationSequence, PeriodType> period;
		private EntityQueryDateFilter<NumerationSequence> validTo;
		private EntityQueryNumericFilter<NumerationSequence, Long> currentValue;

		public NumerationSequenceQuery() {
			super(NumerationSequence.class);
			name = createStringFilter(NumerationSequence_.name);
			period = createFilter(NumerationSequence_.period);
			validTo = createDateFilter(NumerationSequence_.validTo);
			currentValue = createNumericFilter(NumerationSequence_.currentValue);
		}

		public EntityQueryStringFilter<NumerationSequence> name() {
			return name;
		}

		public EntityQuerySimpleFilter<NumerationSequence, PeriodType> period() {
			return period;
		}

		public EntityQueryDateFilter<NumerationSequence> validTo() {
			return validTo;
		}

		public EntityQueryNumericFilter<NumerationSequence, Long> currentValue() {
			return currentValue;
		}
	}

	@AllArgsConstructor(access = AccessLevel.MODULE)
	public enum PeriodType {
		//@formatter:off
		NONE,
		DAY,
		MONTH,
		QUARTER,
		YEAR;
		//@formatter:on

		public String getName() {
			NumerationMessagesBundle messages = LocaleUtils.getMessages(NumerationMessagesBundle.class);

			switch (this) {
				case NONE:
					return messages.numerationPeriodNone();
				case DAY:
					return messages.numerationPeriodDay();
				case MONTH:
					return messages.numerationPeriodMonth();
				case QUARTER:
					return messages.numerationPeriodQuarter();
				case YEAR:
					return messages.numerationPeriodYear();
				default:
					throw new SystemException("Unsupported PeriodType");
			}
		}

		public Date nextValidToDate(Date date) {
			LocalDate currentDate = ChronoUtils.toLocalDate(currentValidToDate(toDate(date)));
			LocalDate nextDate = null;
			switch (this) {
			case DAY:
				nextDate = currentDate.plusDays(1);
				break;
			case MONTH:
				nextDate = currentDate.plusMonths(1);
				break;
			case QUARTER:
				nextDate = lastDayOfQuarter(currentDate).plusMonths(3);
				break;
			case YEAR:
				nextDate = currentDate.plusYears(1);
				break;
			case NONE:
				return null;
			}
			return ChronoUtils.fromLocalDate(nextDate);
		}

		public Date currentValidToDate(Date date) {
			LocalDate localDate = ChronoUtils.toLocalDate(toDate(date));
			LocalDate currentDate = null;
			switch (this) {
			case DAY:
				currentDate = localDate;
				break;
			case MONTH:
				currentDate = localDate.with(TemporalAdjusters.lastDayOfMonth());
				break;
			case QUARTER:
				currentDate = lastDayOfQuarter(localDate);
				break;
			case YEAR:
				currentDate = localDate.with(TemporalAdjusters.lastDayOfYear());
				break;
			case NONE:
				return null;
			}
			return ChronoUtils.fromLocalDate(currentDate);
		}

		private LocalDate lastDayOfQuarter(LocalDate date) {
			Month quarterEnd = date.getMonth().firstMonthOfQuarter().plus(2);
			return LocalDate.of(date.getYear(), quarterEnd, quarterEnd.length(date.isLeapYear()));
		}

		// java.sql.Date.toInstant бросает java.lang.UnsupportedOperationException, поэтому создаем экземпляр
		// java.util.Date
		private Date toDate(Date date) {
			return new Date(date.getTime());
		}
	}

	private static final long serialVersionUID = -4466035992545734011L;
}
