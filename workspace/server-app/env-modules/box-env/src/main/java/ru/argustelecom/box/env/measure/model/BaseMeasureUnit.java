package ru.argustelecom.box.env.measure.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

/**
 * Базовая единица измерения - она же описатель группы единиц
 */
@Entity
@Access(AccessType.FIELD)
public class BaseMeasureUnit extends MeasureUnit {

	@Column(length = 64)
	private String groupName;

	@OneToMany(mappedBy = "group")
	@OrderBy("factor")
	private List<DerivedMeasureUnit> derivedUnits = new ArrayList<>();

	protected BaseMeasureUnit() {
		super();
	}

	public BaseMeasureUnit(Long id) {
		super(id);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<DerivedMeasureUnit> getDerivedUnits() {
		return Collections.unmodifiableList(derivedUnits());
	}

	protected List<DerivedMeasureUnit> derivedUnits() {
		return derivedUnits;
	}

	@Override
	@Transient
	public BaseMeasureUnit getGroup() {
		return this;
	}

	@Override
	public long toBase(long value) {
		// мы сами себе базовая единица
		return value;
	}

	@Override
	public long toBase(double value) {
		return Double.valueOf(value).longValue();
	}

	@Override
	public double fromBase(long value) {
		// мы сами себе базовая единица
		return value;
	}

	@Override
	public long fromBaseFloor(long value) {
		return value;
	}

	@Override
	public String fromBaseAsString(long value) {
		return String.valueOf(value);
	}

	@Override
	public String getObjectName() {
		return groupName;
	}

	private static final long serialVersionUID = -7953638964537261987L;
}
