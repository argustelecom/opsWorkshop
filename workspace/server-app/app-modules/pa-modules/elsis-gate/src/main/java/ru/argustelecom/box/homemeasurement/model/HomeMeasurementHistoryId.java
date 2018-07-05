package ru.argustelecom.box.homemeasurement.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Embeddable
public class HomeMeasurementHistoryId implements Serializable {

	private static final long serialVersionUID = -2191570028184070075L;

	private Long registryId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "history_timestamp")
	private Date date;

	protected HomeMeasurementHistoryId() {
	}

	protected HomeMeasurementHistoryId(Long registryId, Date date) {
		this.registryId = registryId;
		this.date = date;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		HomeMeasurementHistoryId another = (HomeMeasurementHistoryId) obj;
		// formatter:off
		return new EqualsBuilder().append(this.getRegistryId(), another.getRegistryId())
				.append(this.getDate(), another.getDate()).isEquals();
		// formatter:on
	}

	@Override
	public int hashCode() {
		// formatter:off
		return new HashCodeBuilder().append(getRegistryId()).append(getDate()).toHashCode();
		// formatter:on
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Long getRegistryId() {
		return registryId;
	}

	public Date getDate() {
		return date;
	}

}