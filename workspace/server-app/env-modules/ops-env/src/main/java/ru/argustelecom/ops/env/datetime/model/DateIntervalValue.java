package ru.argustelecom.ops.env.datetime.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Range;

@Embeddable
@Access(AccessType.FIELD)
public class DateIntervalValue implements Serializable, Cloneable {

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@Transient
	private Range<Date> dateRange;

	protected DateIntervalValue() {
		super();
	}

	public DateIntervalValue(Date startDate, Date endDate) {
		this.startDate = checkNotNull(startDate);
		this.endDate = checkNotNull(endDate);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = checkNotNull(startDate);
		this.dateRange = null;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = checkNotNull(endDate);
		this.dateRange = null;
	}

	protected Range<Date> getDateRange() {
		if (dateRange == null)
			dateRange = Range.closed(startDate, endDate);
		return dateRange;
	}

	public boolean contains(Date date) {
		return getDateRange().contains(date);
	}

	public boolean encloses(DateIntervalValue other) {
		return getDateRange().encloses(other.getDateRange());
	}

	public boolean intersects(DateIntervalValue other) {
		return getDateRange().isConnected(other.getDateRange());
	}

	public DateIntervalValue intersection(DateIntervalValue other) {
		if (!intersects(other))
			return null;

		return fromRange(getDateRange().intersection(other.getDateRange()));
	}

	public DateIntervalValue union(DateIntervalValue other) {
		if (!intersects(other))
			return null;

		return span(other);
	}

	public DateIntervalValue span(DateIntervalValue other) {
		return fromRange(getDateRange().span(other.getDateRange()));
	}

	protected DateIntervalValue fromRange(Range<Date> range) {
		return new DateIntervalValue(range.lowerEndpoint(), range.upperEndpoint());
	}

	@Override
	public DateIntervalValue clone() {
		return new DateIntervalValue(getStartDate(), getEndDate());
	}

	@Override
	public int hashCode() {
		return Objects.hash(startDate, endDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		DateIntervalValue other = (DateIntervalValue) obj;
		if (!Objects.equals(this.getStartDate(), other.getStartDate()))
			return false;
		if (!Objects.equals(this.getEndDate(), other.getEndDate()))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0,date}..{1,date}", getStartDate(), getEndDate());
	}

	private static final long serialVersionUID = -6844925557765922321L;
}
