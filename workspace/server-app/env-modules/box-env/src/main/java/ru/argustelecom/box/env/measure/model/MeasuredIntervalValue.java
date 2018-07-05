package ru.argustelecom.box.env.measure.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.google.common.collect.Range;

@Embeddable
@Access(AccessType.FIELD)
public class MeasuredIntervalValue implements Serializable, Cloneable, Measurable {

	@Column
	private Long startStoredValue;

	@Column
	private Long endStoredValue;

	// Не переименовыывай это поле в measureUnit, т.к. тогда этот персистентый атрибут будет игнорироваться при
	// использовании в сущности MeasuredIntervalProperty из-за совпадения по имени с персистентынм атрибутом самой
	// сущности
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private MeasureUnit storedUnit;

	@Transient
	private Range<Long> valueRange;

	protected MeasuredIntervalValue() {
	}

	public MeasuredIntervalValue(double startValue, double endValue, MeasureUnit measureUnit) {
		this.storedUnit = checkNotNull(measureUnit);

		this.startStoredValue = measureUnit.toBase(startValue);
		this.endStoredValue = measureUnit.toBase(endValue);
	}

	public MeasuredIntervalValue(long startStoredValue, long endStoredValue, MeasureUnit measureUnit) {
		this.storedUnit = checkNotNull(measureUnit);

		this.startStoredValue = startStoredValue;
		this.endStoredValue = endStoredValue;
	}

	public MeasuredIntervalValue(MeasuredIntervalValue proto, MeasureUnit measureUnit) {
		checkArgument(measureUnit.isConvertibleFrom(proto.getMeasureUnit()));

		this.storedUnit = measureUnit;
		this.startStoredValue = proto.getStartStoredValue();
		this.endStoredValue = proto.getEndStoredValue();
	}

	public boolean isConvertibleForm(MeasuredIntervalValue other) {
		return storedUnit.isConvertibleFrom(other.getMeasureUnit());
	}

	public Long getStartStoredValue() {
		return startStoredValue;
	}

	protected void setStartStoredValue(Long startStoredValue) {
		this.startStoredValue = startStoredValue;
		this.valueRange = null;
	}

	public Long getEndStoredValue() {
		return endStoredValue;
	}

	protected void setEndStoredValue(Long endStoredValue) {
		this.endStoredValue = endStoredValue;
		this.valueRange = null;
	}

	@Override
	public MeasureUnit getMeasureUnit() {
		return storedUnit;
	}

	protected void setMeasureUnit(MeasureUnit measureUnit) {
		this.storedUnit = measureUnit;
	}

	public double getStartValue() {
		return storedUnit.fromBase(startStoredValue);
	}

	public String getStartValueAsString() {
		return storedUnit.fromBaseAsString(startStoredValue);
	}

	public MeasuredValue getStartPoint() {
		MeasuredValue startPoint = new MeasuredValue();
		startPoint.setMeasureUnit(getMeasureUnit());
		startPoint.setStoredValue(getStartStoredValue());
		return startPoint;
	}

	public double getEndValue() {
		return storedUnit.fromBase(endStoredValue);
	}

	public String getEndValueAsString() {
		return storedUnit.fromBaseAsString(endStoredValue);
	}

	public MeasuredValue getEndPoint() {
		MeasuredValue endPoint = new MeasuredValue();
		endPoint.setMeasureUnit(getMeasureUnit());
		endPoint.setStoredValue(getEndStoredValue());
		return endPoint;
	}

	public void setStartValue(double startValue) {
		setStartStoredValue(storedUnit.toBase(startValue));
	}

	public void setEndValue(double endValue) {
		setEndStoredValue(storedUnit.toBase(endValue));
	}

	public void setValue(MeasuredIntervalValue proto) {
		checkArgument(isConvertibleForm(proto));
		setStartStoredValue(proto.getStartStoredValue());
		setEndStoredValue(proto.getEndStoredValue());
	}

	protected Range<Long> getValueRange() {
		if (valueRange == null)
			valueRange = Range.closed(startStoredValue, endStoredValue);
		return valueRange;
	}

	public boolean contains(MeasuredValue value) {
		if (!storedUnit.isConvertibleFrom(value.getMeasureUnit()))
			return false;

		return getValueRange().contains(value.getStoredValue());
	}

	public boolean encloses(MeasuredIntervalValue other) {
		if (isConvertibleForm(other))
			return false;

		return getValueRange().encloses(other.getValueRange());
	}

	public boolean intersects(MeasuredIntervalValue other) {
		if (!isConvertibleForm(other))
			return false;

		return getValueRange().isConnected(other.getValueRange());
	}

	public MeasuredIntervalValue intersection(MeasuredIntervalValue other) {
		if (!intersects(other))
			return null;

		return fromRange(getValueRange().intersection(other.getValueRange()));
	}

	public MeasuredIntervalValue union(MeasuredIntervalValue other) {
		if (!intersects(other))
			return null;

		return span(other);
	}

	public MeasuredIntervalValue span(MeasuredIntervalValue other) {
		return fromRange(getValueRange().span(other.getValueRange()));
	}

	protected MeasuredIntervalValue fromRange(Range<Long> range) {
		MeasuredIntervalValue result = new MeasuredIntervalValue();
		result.setMeasureUnit(this.getMeasureUnit());
		result.setStartStoredValue(range.lowerEndpoint());
		result.setEndStoredValue(range.upperEndpoint());
		return result;
	}

	public MeasuredValue[] split() {
		return new MeasuredValue[] { getStartPoint(), getEndPoint() };
	}

	@Override
	public MeasuredIntervalValue clone() {
		return new MeasuredIntervalValue(this, storedUnit);
	}

	@Override
	public int hashCode() {
		return Objects.hash(storedUnit, startStoredValue, endStoredValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		MeasuredIntervalValue other = (MeasuredIntervalValue) obj;
		if (!Objects.equals(this.getMeasureUnit(), other.getMeasureUnit()))
			return false;
		if (!Objects.equals(this.getStartStoredValue(), other.getStartStoredValue()))
			return false;
		if (!Objects.equals(this.getEndStoredValue(), other.getEndStoredValue()))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return format("{0}..{1} {2}", getStartValueAsString(), getEndValueAsString(), storedUnit.getObjectName());
	}

	private static final long serialVersionUID = 1056712451042302403L;
}
