package ru.argustelecom.box.homemeasurement.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Сущность, которая описывает результаты последнего измерения некоторого счётчика. Необходима для интеграции с Light
 * House.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "elsis")
public class HomeMeasurement implements Serializable {

	private static final long serialVersionUID = -2021822390990825967L;

	@Id
	private Long registryId;

	private String aggregateId;

	private String registryName;

	private String registryType;

	private String registrySerialNumber;

	private BigDecimal lastMeasurementValue;

	private Date lastMeasurementTime;

	private String measurementUnit;

	protected HomeMeasurement() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		HomeMeasurement other = (HomeMeasurement) obj;
		return new EqualsBuilder().append(this.getRegistryId(), other.getRegistryId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getRegistryId()).toHashCode();
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Long getRegistryId() {
		return registryId;
	}

	public String getAggregateId() {
		return aggregateId;
	}

	public String getRegistryName() {
		return registryName;
	}

	public String getRegistryType() {
		return registryType;
	}

	public String getRegistrySerialNumber() {
		return registrySerialNumber;
	}

	public BigDecimal getLastMeasurementValue() {
		return lastMeasurementValue;
	}

	public Date getLastMeasurementTime() {
		return lastMeasurementTime;
	}

	public String getMeasurementUnit() {
		return measurementUnit;
	}

}