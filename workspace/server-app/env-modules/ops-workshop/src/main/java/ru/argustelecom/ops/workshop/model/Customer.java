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
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author k.koropovskiy
 */
@Entity
public class Customer {

	@Id
	@GeneratedValue
	@Getter @Setter
	private int id;

	@Column
	@Getter @Setter
	private String name;

	@Column
	@Getter @Setter
	private String jiraName;

	@Column
	@Getter @Setter
	private String jiraProject;

	@Override public String toString() {
		return "Customer{" +
				"id=" + id +
				", name='" + name + '\'' +
				", jiraName='" + jiraName + '\'' +
				", jiraProject='" + jiraProject + '\'' +
				", products=" + (products == null ? "NULL" : "["+products.stream().map(Product::getName).collect(Collectors.joining(","))+"]") +
				", serverInstances.size=" + serverInstances.size() +
				'}';
	}

	@ManyToMany(cascade = CascadeType.ALL)
	@Getter @Setter
	@JoinTable(name = "customer_product",
			joinColumns = @JoinColumn(name = "customer_id"),
			inverseJoinColumns = @JoinColumn(name = "product_id"))
	private List<Product> products;

	@OneToMany
	@Getter @Setter
	private List<ApplicationServerInstance> serverInstances;

	private Customer() {
		this.products = new ArrayList<>();
		this.serverInstances = new ArrayList<>();
	}

	public Customer(String name, String jiraName, String jiraProject) {
		this();
		this.name = name;
		this.jiraName = jiraName;
		this.jiraProject = jiraProject;
	}

	public Boolean addProduct(Product product) {
		return products.add(product);
	}
}
