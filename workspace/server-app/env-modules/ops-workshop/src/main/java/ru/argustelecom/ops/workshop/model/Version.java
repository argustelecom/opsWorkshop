package ru.argustelecom.ops.workshop.model;

import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author k.koropovskiy
 */
@Entity
@Table(schema = "ops", name = "version")
@NoArgsConstructor
public class Version {

	@Id
	@GeneratedValue
	private int id;

	@Column(name = "version_name")
	private String name;

	public Version(String name){
		this.name = name;
	};

	@Override
	public String toString() {
		return "Version{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}


}
