package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author k.koropovskiy
 */
@Entity
@Table(schema = "ops", name = "version")
@NoArgsConstructor
public class Version extends OpsSuperClass {

	@Column(name = "version_name", nullable = false)
	@Getter
	@Setter
	private String name;

	@Column(name = "fixation_date", nullable = false)
	@Getter
	@Setter
	private Timestamp fixationDate;

	@Column(name = "shipment_date", nullable = false)
	@Getter
	@Setter
	private Timestamp shipmentDate;

	@Column(name = "version_status", nullable = false)
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private VersionStatus status;

	@Column(name = "jira_task")
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private String jiraTask;


	public Version(String name, Timestamp fixationDate, Timestamp shipmentDate, VersionStatus status, String jiraTask){
		this.name = name;
		this.fixationDate = fixationDate;
		this.shipmentDate = shipmentDate;
		this.status = status;
		this.jiraTask = jiraTask;
	};
	//Достаем последниюю версию для заполнения параметров по умолчанию
	public static Version getLastVersion(EntityManager em){
		Query query = em.createQuery("SELECT MAX(id) FROM Version");
		int maxId = (int)query.getSingleResult();
		return em.find(Version.class,maxId);
	};

	@Override
	public String toString() {
		return "Version{" +
				"id=" + getId() +
				", name='" + name + '\'' +
				'}';
	}


}
