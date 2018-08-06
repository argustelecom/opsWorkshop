package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author k.koropovskiy
 */
@Entity
public class Artifact {

	@Id
	@GeneratedValue
	@Getter @Setter
	private int id;

	@Column
	@Getter @Setter
	private String name;

	@Column
	@Getter @Setter
	private String gitRepository;

	@ManyToMany
	@Getter @Setter
	@JoinTable(name = "artifact_product",
			joinColumns = @JoinColumn(name = "artifact_id"),
			inverseJoinColumns = @JoinColumn(name = "product_id"))
	private Collection<Product> products;

	private Artifact(){
		this.products = new ArrayList<>();
	}

	public Artifact(String name, String gitRepository) {
		this();
		this.name = name;
		this.gitRepository = gitRepository;
	}
}
