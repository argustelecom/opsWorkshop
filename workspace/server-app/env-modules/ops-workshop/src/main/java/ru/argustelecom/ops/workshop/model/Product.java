package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author k.koropovskiy
 */
@Entity
public class Product {
	@Id
	@GeneratedValue
	@Getter @Setter
	private int id;

	@Column
	@Getter @Setter
	private String name;

	@Column
	@Getter @Setter
	private String jiraProject;

	@Column
	@Getter @Setter
	private String jiraComponent;

	@ManyToMany(mappedBy = "products")
	@Getter @Setter
	private List<Customer> customers;

	@ManyToMany(mappedBy = "products")
	@Getter @Setter
	private List<Artifact> artifacts;

	@ManyToMany(mappedBy = "products")
	@Getter @Setter
	private List<Team> teams;

	private Product(){
		customers = new ArrayList<>();
		artifacts = new ArrayList<>();
	}

	public Product(String name, String jiraProject, String jiraComponent) {
		this();
		this.name = name;
		this.jiraProject = jiraProject;
		this.jiraComponent = jiraComponent;
	}

	@Override public String toString() {
		return "Product{" +
				"id=" + id +
				", name='" + name + '\'' +
				", jiraProject='" + jiraProject + '\'' +
				", jiraComponent='" + jiraComponent + '\'' +
				", customers=" + (customers == null ? "NULL" : "[" + customers.stream().map(Customer::getName).collect(Collectors.joining(",")) + "]") +
				", artifacts=" + (artifacts == null ? "NULL" : "[" + artifacts.stream().map(Artifact::getName).collect(Collectors.joining(",")) + "]") +
				", teams=" + (teams == null ? "NULL" : "[" + teams.stream().map(Team::getName).collect(Collectors.joining(","))+ "]") +
				'}';
	}
}
