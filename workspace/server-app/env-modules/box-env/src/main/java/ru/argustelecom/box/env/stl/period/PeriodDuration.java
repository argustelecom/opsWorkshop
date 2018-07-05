package ru.argustelecom.box.env.stl.period;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.TemporalAmount;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

@Embeddable
@Access(AccessType.FIELD)
public class PeriodDuration implements Comparable<PeriodDuration>, Serializable {

	private static final long serialVersionUID = -8203285709229996274L;

	@Column(nullable = false)
	private int amount;

	@Enumerated(EnumType.STRING)
	private PeriodUnit unit;

	@Transient
	private transient TemporalAmount temporalAmount;

	@Transient
	private transient Duration duration;

	protected PeriodDuration() {
	}

	protected PeriodDuration(int amount, PeriodUnit unit) {
		checkArgument(amount > 0);
		checkArgument(unit != null);

		this.amount = amount;
		this.unit = unit;
	}

	public static PeriodDuration of(int amount, PeriodUnit unit) {
		return new PeriodDuration(amount, unit);
	}

	public static PeriodDuration ofHours(int amount) {
		return new PeriodDuration(amount, PeriodUnit.HOUR);
	}

	public static PeriodDuration ofDays(int amount) {
		return new PeriodDuration(amount, PeriodUnit.DAY);
	}

	public static PeriodDuration ofMonths(int amount) {
		return new PeriodDuration(amount, PeriodUnit.MONTH);
	}

	public static PeriodDuration ofQuarters(int amount) {
		return new PeriodDuration(amount, PeriodUnit.QUARTER);
	}

	public static PeriodDuration ofSemesters(int amount) {
		return new PeriodDuration(amount, PeriodUnit.SEMESTER);
	}

	public static PeriodDuration ofYears(int amount) {
		return new PeriodDuration(amount, PeriodUnit.YEAR);
	}

	public int getAmount() {
		return amount;
	}

	public PeriodUnit getUnit() {
		return unit;
	}

	public TemporalAmount getTemporalAmount() {
		if (temporalAmount == null) {
			temporalAmount = unit.amountOf(amount);
		}
		return temporalAmount;
	}

	public Duration getDuration() {
		if (duration == null) {
			duration = unit.durationOf(amount);
		}
		return duration;
	}

	@Override
	public int compareTo(PeriodDuration that) {
		return this.getDuration().compareTo(that.getDuration());
	}

	public boolean greater(PeriodDuration that) {
		return this.compareTo(that) > 0;
	}

	public boolean greaterOrEquals(PeriodDuration that) {
		return this.compareTo(that) >= 0;
	}

	public boolean less(PeriodDuration that) {
		return this.compareTo(that) < 0;
	}

	public boolean lessOrEquals(PeriodDuration that) {
		return this.compareTo(that) <= 0;
	}

	@Override
	public int hashCode() {
		return getDuration().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		PeriodDuration that = (PeriodDuration) obj;
		return this.getDuration().equals(that.getDuration());
	}

	@Override
	public String toString() {
		return String.format("%d %s", getAmount(), getUnit());
	}

}