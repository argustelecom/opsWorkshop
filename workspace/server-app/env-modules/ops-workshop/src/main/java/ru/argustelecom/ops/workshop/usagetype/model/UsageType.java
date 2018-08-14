package ru.argustelecom.ops.workshop.usagetype.model;

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
@Table(name = "usage_type", schema = "ops")
public class UsageType implements Serializable {

	private static final long serialVersionUID = 2594037403678943103L;

	@Id
	@Getter
	@Setter
	@GeneratedValue
	private long id;

	@Column
	@Getter
	@Setter
	private String name;

	@Column(name = "abbreviation",length = 16)
	@Getter
	@Setter
	private String abbreviation;

	public UsageType(){
	}

	public UsageType(String name, String abbreviation){
		this();
		this.name = name;
		this.abbreviation = abbreviation;
	}
}
