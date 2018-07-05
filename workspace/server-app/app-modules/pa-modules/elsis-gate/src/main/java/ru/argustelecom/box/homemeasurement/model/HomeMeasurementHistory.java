package ru.argustelecom.box.homemeasurement.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Сущность описывающая историю измерений для конкретного счётчика. Необходима для интеграции с Light House.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "elsis")
public class HomeMeasurementHistory implements Serializable {

	@EmbeddedId
	private HomeMeasurementHistoryId id;

	@Column(name = "history_value")
	private BigDecimal value;

	protected HomeMeasurementHistory() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		HomeMeasurementHistory other = (HomeMeasurementHistory) obj;
		return new EqualsBuilder().append(this.getId(), other.getId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).toHashCode();
	}

	public Date getDate() {
		return getId().getDate();
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public HomeMeasurementHistoryId getId() {
		return id;
	}

	public BigDecimal getValue() {
		return value;
	}

}