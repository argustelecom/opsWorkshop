package ru.argustelecom.ops.workshop.model;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author k.koropovskiy
 */
@Entity
@Table(schema = "ops", name = "customer")
@NoArgsConstructor
public class Customer extends OpsSuperClass {

	@Column(name = "name", length = 128, nullable = false)
	@Getter
	@Setter
	private String name;

	@Column(name = "jira_name", length = 128)
	@Getter
	@Setter
	private String jiraName;

	@Column(name = "jira_project", length = 8)
	@Getter
	@Setter
	private String jiraProject;

	@ManyToMany
	@Getter
	@JoinTable(schema = "ops",
			name = "customer_product",
			joinColumns = @JoinColumn(name = "customer_id"),
			inverseJoinColumns = @JoinColumn(name = "product_id"))
	private List<Product> products = new ArrayList<>();

//	@OneToMany
//	@Getter
//	private List<ApplicationServerInstance> serverInstances = new ArrayList<>();

	public Customer(String name, String jiraName, String jiraProject) {
		this.name = name;
		this.jiraName = jiraName;
		this.jiraProject = jiraProject;
	}

	@Override
	public String toString() {
		return "Customer{" + "id=" + getId() + ", name='" + name + '\'' + ", jiraName='" + jiraName + '\''
				+ ", jiraProject='" + jiraProject + '\'' + ", products="
				+ (products == null ? "NULL"
				: "[" + products.stream().map(Product::getName).collect(Collectors.joining(",")) + "]")
//				+ ", serverInstances.size=" + serverInstances.size()
				+ '}';
	}

	public Boolean addProduct(Product product) {
		return products.add(product);
	}
}
