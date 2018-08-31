package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Query;
import java.util.Date;

/**
 * @author k.koropovskiy + a.lapygin
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

	@Column(name = "jira_task")
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private String jiraTask;

	private static final String findLastVersionQuery = "findLastVersionQuery";

	public Version(String name, Date fixationDate, Date shipmentDate, VersionStatus status, String jiraTask){
		this.name = name;
		this.fixationDate = fixationDate;
		this.shipmentDate = shipmentDate;
		this.status = status;
		this.jiraTask = jiraTask;
	};

	//Достаем последниюю версию для заполнения параметров по умолчанию
	@NamedQuery(
		name = findLastVersionQuery,
		query = "SELECT MAX(id) FROM ops.version"
	)
	public static Version findLastVersion(EntityManager em){
		Query query = em.createNamedQuery(findLastVersionQuery);
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
