package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jboss.narayana.compensations.api.CancelOnFailure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author k.koropovskiy
 */
@Entity
@Table(schema = "ops", name = "usage_type")
@NoArgsConstructor
public class UsageType {
	@Id
	@Getter
	@Column(name = "key", length = 128, nullable = false)
	private String key;

	@Column(name = "name", length = 128, nullable = false)
	@Getter
	private String name;

	public UsageType(String key, String name) {
		this.key = key;
		this.name = name;
	}
}
