package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author k.koropovskiy
 */
@Entity
@Table(schema = "ops", name = "team")
@NoArgsConstructor
public class Team extends OpsSuperClass {

	@Column(name = "name")
	@Getter
	@Setter
	private String name;

	@Column(name = "jira_component")
	@Getter
	@Setter
	private String jiraComponent;

	@ManyToMany
	@Getter
	@JoinTable(
			schema = "ops",
			name = "team_teammate",
			joinColumns = @JoinColumn(name = "team_id"),
			inverseJoinColumns = @JoinColumn(name = "teammate_id"))
	private List<Teammate> teammates = new ArrayList<>();

	@ManyToMany
	@Getter
	@JoinTable(
			schema = "ops",
			name = "team_product",
			joinColumns = @JoinColumn(name = "team_id"),
			inverseJoinColumns = @JoinColumn(name = "product_id"))
	private List<Product> products = new ArrayList<>();

	/*
	 * @ManyToMany(cascade = CascadeType.ALL)
	 * 
	 * @JoinTable( name = "team_appserver", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns
	 * = @JoinColumn(name = "asi_id"))
	 * 
	 * @Getter private Collection<ApplicationServerInstance> applicationServerInstances = new ArrayList<>();
	 */
	public Team(String name, String jiraComponent) {
		this.jiraComponent = jiraComponent;
		this.name = name;
	}

	public Boolean addTeammate(Teammate teammate) {
		return teammates.add(teammate);
	}

	public Boolean addProduct(Product product) {
		return products.add(product);
	}

	@Override
	public String toString() {
		return "Team{" + "id=" + getId() + ", name='" + name + '\'' + ", jiraComponent='" + jiraComponent + '\''
				+ ", teammates="
				+ (teammates == null ? "NULL"
						: "[" + teammates.stream().map(Teammate::getJiraName).collect(Collectors.joining(",")) + "]")
				+ ", products="
				+ (products == null ? "NULL"
						: "[" + products.stream().map(Product::getName).collect(Collectors.joining(",")) + "]")
//				+ ", applicationServerInstances.size=" + applicationServerInstances.size()
				+ '}';
	}

}
