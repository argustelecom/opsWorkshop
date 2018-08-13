package ru.argustelecom.ops.workshop.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author k.koropovskiy
 */
@Entity
@Table(schema = "ops", name = "artifact")
@NoArgsConstructor
public class Artifact extends OpsSuperClass {

	@Column(name = "name", length = 128, nullable = false)
	@Getter
	@Setter
	private String name;

	@Column(name = "git_repository", length = 128)
	@Getter
	@Setter
	private String gitRepository;

	@ManyToMany
	@Getter
	@JoinTable(
			schema = "ops",
			name = "artifact_product",
			joinColumns = @JoinColumn(name = "artifact_id"),
			inverseJoinColumns = @JoinColumn(name = "product_id"))
	private Collection<Product> products = new ArrayList<>();;

	public Artifact(String name, String gitRepository) {
		this.name = name;
		this.gitRepository = gitRepository;
	}
}
