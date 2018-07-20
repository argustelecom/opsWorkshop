package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author k.koropovskiy
 */
@Entity
public class Version {

	@Id
	@GeneratedValue
	private int id;

	@Column
	private String name;

	@Override public String toString() {
		return "Version{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}

	public Version(String name){
		this();
		this.name = name;
	};

	private Version(){}

}
