package ru.argustelecom.ops.workshop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.argustelecom.ops.workshop.application.server.model.ApplicationServer;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

	@ManyToMany(mappedBy = "teams")
	@Getter
	private Set<ApplicationServer> applicationServers = new LinkedHashSet<>();

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

	/**
	 * Добавляет СП в коллекцию. ВНИМАНИЕ! у сущности СП есть колекция teams, если хочешь добавить сервер для команды, то используй
	 * метод {@link ApplicationServer#addTeam(Team)} у сущности СП.
	 * @param newAppServer
	 * @return
	 */
	public boolean addApplicationServer(ApplicationServer newAppServer){
		return applicationServers.add(newAppServer);
	}

	/**
	 * Удаляет СП из коллекции applicationServer. ВНИМАНИЕ! у сущности СП есть колекция teams, если хочешь удалить сервер из коллекции applicationServer,
	 * то используй метод {@link ApplicationServer#removeTeam(Team)} у сущности СП.
	 * @param appServer
	 * @return
	 */
	public boolean removeApplicationServer(ApplicationServer appServer) {
		return applicationServers.remove(appServer);
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
				+ ", applicationServers.size=" + applicationServers.size()
				+ '}';
	}

}
