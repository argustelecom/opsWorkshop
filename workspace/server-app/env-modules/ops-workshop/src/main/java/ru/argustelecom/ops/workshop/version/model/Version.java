package ru.argustelecom.ops.workshop.version.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author v.semchenko
 */
@Entity
@Table(name = "version", schema = "ops")
public class Version implements Serializable {

	private static final long serialVersionUID = -7419006210824218514L;

	@Id
	@Getter
	@Setter
	@GeneratedValue
	private long id;

	@Column(name = "version_name")
	@Getter
	@Setter
	private String versionName;

	public Version() {
	}

	public Version(String version){
		this.versionName = version;
	}

}
