package ru.argustelecom.ops.workshop.customer.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.ops.workshop.product.model.Product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * |Имя	            |Тип	|Обяз?	|Откуда данные|
 * ---------------------------------------------------------
 * |Имя	            |Текст	|ДА	    |
 * |Имя в Jira	    |Текст	|ДА	    |
 * |Проект в Jira	|Текст	|НЕТ	|
 * |Продукты	    |список	|НЕТ	|Справочник продуктов|
 *
 * v.semchenko
 */

@Entity
@Table(name = "customer", schema = "ops")
public class Customer implements Serializable {

	private static final long serialVersionUID = -3600281175035559906L;

	@Id
	@Getter
	@Setter
	@GeneratedValue
	private long id;

	@Column(nullable = false)
	@Getter
	@Setter
	private String name;

	@ManyToMany(mappedBy = "customers")
	@Getter
	@Setter
	private Set<Product> products;

	public Customer() {
		this.products = new LinkedHashSet<>();
	}

	public Customer(String name) {
		this();
		this.name = name;
	}

	@Override
	public String toString() {
		return "Customer{" +
				"\nid=" + id +
				", \nname='" + name + '\'' +
//				", \nappservers=" + appservers.stream().map(s -> s.getAppServerName()).toArray() +*/
				", \nproducts=" + products.stream().map(p -> p.getName()).toArray() +
				'}';
	}
}
