package ru.argustelecom.box.env.measure.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Embeddable
@Access(AccessType.FIELD)
public class MeasuredValue implements Serializable, Comparable<MeasuredValue>, Cloneable, Measurable {

	@Column
	private Long storedValue;

	// Не переименовыывай это поле в measureUnit, т.к. тогда этот персистентый атрибут будет игнорироваться при
	// использовании в сущности MeasuredProperty из-за совпадения по имени с персистентынм атрибутом самой
	// сущности
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private MeasureUnit storedUnit;

	protected MeasuredValue() {
	}

	public MeasuredValue(double value, MeasureUnit measureUnit) {
		this.storedUnit = checkNotNull(measureUnit);
		this.storedValue = measureUnit.toBase(value);
	}

	public MeasuredValue(long storedValue, MeasureUnit measureUnit) {
		this.storedUnit = checkNotNull(measureUnit);
		this.storedValue = storedValue;
	}

	public MeasuredValue(MeasuredValue proto, MeasureUnit measureUnit) {
		checkArgument(measureUnit.isConvertibleFrom(proto.getMeasureUnit()));

		this.storedUnit = measureUnit;
		this.storedValue = proto.getStoredValue();
	}

	public boolean isConvertibleForm(MeasuredValue other) {
		return storedUnit.isConvertibleFrom(other.getMeasureUnit());
	}

	public Long getStoredValue() {
		return storedValue;
	}

	protected void setStoredValue(Long storedValue) {
		this.storedValue = storedValue;
	}

	@Override
	public MeasureUnit getMeasureUnit() {
		return storedUnit;
	}

	protected void setMeasureUnit(MeasureUnit measureUnit) {
		this.storedUnit = measureUnit;
	}

	public double getValue() {
		return storedUnit.fromBase(storedValue);
	}

	public String getValueAsString() {
		return storedUnit.fromBaseAsString(storedValue);
	}

	public void setValue(double value) {
		this.storedValue = storedUnit.toBase(value);
	}

	public void setValue(MeasuredValue proto) {
		checkArgument(isConvertibleForm(proto));
		this.storedValue = proto.getStoredValue();
	}

	public void plus(MeasuredValue other) {
		checkArgument(isConvertibleForm(other));
		this.storedValue += other.getStoredValue();
	}

	public void minus(MeasuredValue other) {
		checkArgument(isConvertibleForm(other));
		this.storedValue -= other.getStoredValue();
	}

	public MeasuredIntervalValue makeIntervalWith(MeasuredValue other) {
		if (!isConvertibleForm(other))
			return null;

		return new MeasuredIntervalValue(this.getStoredValue(), other.getStoredValue(), getMeasureUnit());
	}

	@Override
	public int compareTo(MeasuredValue o) {
		checkArgument(isConvertibleForm(o));
		return Long.compare(storedValue, o.getStoredValue());
	}

	@Override
	public MeasuredValue clone() {
		return new MeasuredValue(this, storedUnit);
	}

	@Override
	public int hashCode() {
		return Objects.hash(storedUnit, storedValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		MeasuredValue other = (MeasuredValue) obj;
		if (!Objects.equals(storedUnit, other.getMeasureUnit()))
			return false;
		if (!Objects.equals(storedValue, other.getStoredValue()))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0} {1}", getValueAsString(), storedUnit.getObjectName());
	}

	private static final long serialVersionUID = 2179616575484236873L;
}
