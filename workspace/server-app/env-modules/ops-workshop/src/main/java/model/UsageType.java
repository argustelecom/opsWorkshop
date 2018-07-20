package model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author k.koropovskiy
 */
@Entity
public class UsageType {
	@Id
	@Getter
	private String key;

	@Column
	@Getter @Setter
	private String name;

	private UsageType(){};

	public UsageType(String key, String name){
		this();
		this.key = key;
		this.name = name;
	}
}
