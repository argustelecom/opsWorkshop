package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author k.koropovskiy
 */
@Entity
@Table(schema = "ops", name = "version")
@NoArgsConstructor
public class Version extends OpsSuperClass {

	@Id
	@Getter
	@Setter
	private String id;

	@Column(name = "version_name", nullable = false)
	@Getter
	@Setter
	private String name;

	@Column(name = "fixation_date", nullable = false)
	@Getter
	@Setter
	private Date fixationDate;

	@Column(name = "shipment_date", nullable = false)
	@Getter
	@Setter
	private Date shipmentDate;

	@Column(name = "version_status", nullable = false)
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private VersionStatus status;


	public Version(String name){
		this.name = name;
	};

	@Override
	public String toString() {
		return "Version{" +
				"id=" + getId() +
				", name='" + name + '\'' +
				'}';
	}


}
