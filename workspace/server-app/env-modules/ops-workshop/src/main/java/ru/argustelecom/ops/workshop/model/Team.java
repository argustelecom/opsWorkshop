package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author k.koropovskiy
 */
@Entity
public class Team {
	@Id
	@GeneratedValue
	@Getter @Setter
	private int id;

	@Column
	@Getter @Setter
	private String name;

	@Column
	@Getter @Setter
	private String jiraComponent;

	@ManyToMany
	@Getter @Setter
	@JoinTable(name = "team_teammate",
			joinColumns = @JoinColumn(name = "team_id"),
			inverseJoinColumns = @JoinColumn(name = "teammate_id"))
	private List<Teammate> teammates;

	@ManyToMany(cascade = CascadeType.ALL)
	@Getter @Setter
	@JoinTable(name = "team_product",
			joinColumns = @JoinColumn(name = "team_id"),
			inverseJoinColumns = @JoinColumn(name = "product_id"))
	private List<Product> products;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "team_appserver",
			joinColumns = @JoinColumn(name = "team_id"),
			inverseJoinColumns = @JoinColumn(name = "asi_id"))
	@Getter @Setter
	private Collection<ApplicationServerInstance> applicationServerInstances;

	private Team() {
		this.teammates = new ArrayList<>();
		this.products = new ArrayList<>();
		this.applicationServerInstances = new ArrayList<>();
	}

	@Override public String toString() {
		return "Team{" +
				"id=" + id +
				", name='" + name + '\'' +
				", jiraComponent='" + jiraComponent + '\'' +
				", teammates=" + (teammates == null ? "NULL" : "["+teammates.stream().map(Teammate::getJiraName).collect(Collectors.joining(","))+"]") +
				", products=" + (products == null ? "NULL" : "["+products.stream().map(Product::getName).collect(Collectors.joining(","))+"]") +
				", applicationServerInstances.size=" + applicationServerInstances.size() +
				'}';
	}

	public Team(String name, String jiraComponent) {
		this();
		this.jiraComponent = jiraComponent;
		this.name = name;
	}

	public Boolean addTeammate(Teammate teammate) {
		return teammates.add(teammate);
	}

	public Boolean addProduct(Product product) {
		return products.add(product);
	}

}
