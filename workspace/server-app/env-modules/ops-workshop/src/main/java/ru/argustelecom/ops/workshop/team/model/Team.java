package ru.argustelecom.ops.workshop.team.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.ops.workshop.application.server.model.ApplicationServer;
import ru.argustelecom.ops.workshop.product.model.Product;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * v.semchenko
 */
@Entity
@Table(schema = "ops")
public class Team implements Serializable {

	private static final long serialVersionUID = 6946170430587351044L;

	@Id
	@Getter
	@Setter
	private long id;

	@Column(name = "name", nullable = false)
	@Getter
	@Setter
	private String name;

//	@Column(name = "jira_team", nullable = false)
//	@Getter @Setter
//	private String nameProjectInJira;

	@Column(name = "jira_component")
	@Getter
	@Setter
	private String componentInJira;

	@OneToMany(mappedBy = "team")
	@Getter
	@Setter
	private Set<Product> products;

	@ManyToMany(mappedBy = "teams")
	@Getter
	@Setter
	private Set<ApplicationServer> appservers;

	public Team() {
		this.name = "";
		this.appservers = new LinkedHashSet<>();
		this.products = new LinkedHashSet<>();
	}

	public Team(String name, String componentInJira) {
		this.name = name;
		this.componentInJira = componentInJira;
		this.appservers = new LinkedHashSet<>();
		this.products = new LinkedHashSet<>();
	}

	public boolean addProduct(Product argusComponent) {
		return this.products.add(argusComponent);
	}

	@Override
	public String toString() {
		return "Team{" +
				"\nid=" + id +
				", \nname='" + name + '\'' +
//				", \nnameProjectInJira='" + nameProjectInJira + '\'' +
				", \ncomponentInJira='" + componentInJira + '\'' +
				", \n\nproducts=" + products.stream().map(m -> m.getName()).toArray() +
				", \n\nappservers=" + appservers.stream().map(s -> s.getAppServerName()).toArray() +
				'}';
	}
}
