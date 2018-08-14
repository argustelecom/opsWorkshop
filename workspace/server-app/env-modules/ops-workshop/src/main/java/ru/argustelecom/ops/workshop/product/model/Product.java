package ru.argustelecom.ops.workshop.product.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.ops.workshop.customer.model.Customer;
import ru.argustelecom.ops.workshop.team.model.Team;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author v.semchenko
 */
@Entity
@Table(name = "product", schema = "ops")
public class Product implements Serializable {

	private static final long serialVersionUID = -3670410157374950459L;

	@Id
	@Getter
	@Setter
	@GeneratedValue
	private long id;

	@Column(nullable = false)
	@Getter
	@Setter
	private String name;

	@ManyToOne
	@Getter
	@Setter
	private Team team;

	@ManyToMany
	@JoinTable(name = "products_customers",
			joinColumns = @JoinColumn(name = "product_id"),
			inverseJoinColumns = @JoinColumn(name = "customer_id"))
	@Getter
	@Setter
	private Set<Customer> customers;


	public Product(){
		this.customers = new LinkedHashSet<>();
	}

	public Product(String name) {
		this();
		this.name = name;
	}

	@Override
	public String toString() {
		return "Product{" +
				"\nid=" + id +
				",\nname='" + name + '\'' +
				",\nteam=" + (team != null ? team.getName() : "") +
				",\n\ncustomers=" + customers.stream().map(c -> c.getName()).toArray() +
				'}';
	}

	public boolean addCustomer(Customer customer){
		return this.customers.add(customer);
	}

}
