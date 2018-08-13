package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author k.koropovskiy
 */
@Entity
@Table(schema = "ops", name = "product")
@NoArgsConstructor
public class Product {
	@Id
	@GeneratedValue
	@Getter
	private int id;

	@Column(name = "name", length = 128, nullable = false)
	@Getter
	@Setter
	private String name;

	@Column(name = "jira_project", length = 8)
	@Getter
	@Setter
	private String jiraProject;

	@Column(name = "jira_component", length = 128)
	@Getter
	@Setter
	private String jiraComponent;

	@ManyToMany(mappedBy = "products")
	@Getter
	private List<Customer> customers = new ArrayList<>();

	@ManyToMany(mappedBy = "products")
	@Getter
	private List<Artifact> artifacts = new ArrayList<>();

	@ManyToMany(mappedBy = "products")
	@Getter
	private List<Team> teams = new ArrayList<>();

	public Product(String name, String jiraProject, String jiraComponent) {
		this.name = name;
		this.jiraProject = jiraProject;
		this.jiraComponent = jiraComponent;
	}

	@Override
	public String toString() {
		return "Product{" + "id=" + id + ", name='" + name + '\'' + ", jiraProject='" + jiraProject + '\''
				+ ", jiraComponent='" + jiraComponent + '\'' + ", customers="
				+ (customers == null ? "NULL"
						: "[" + customers.stream().map(Customer::getName).collect(Collectors.joining(",")) + "]")
				+ ", artifacts="
				+ (artifacts == null ? "NULL"
						: "[" + artifacts.stream().map(Artifact::getName).collect(Collectors.joining(",")) + "]")
				+ ", teams=" + (teams == null ? "NULL"
						: "[" + teams.stream().map(Team::getName).collect(Collectors.joining(",")) + "]")
				+ '}';
	}
}
