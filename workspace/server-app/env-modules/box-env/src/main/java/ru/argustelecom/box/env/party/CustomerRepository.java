package ru.argustelecom.box.env.party;

import static ru.argustelecom.box.env.party.CustomerCategory.COMPANY;
import static ru.argustelecom.box.env.party.CustomerCategory.PERSON;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Individual;
import ru.argustelecom.box.env.party.model.role.Individual.IndividualQuery;
import ru.argustelecom.box.env.party.model.role.Organization;
import ru.argustelecom.box.env.party.model.role.Organization.OrganizationQuery;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class CustomerRepository implements Serializable {

	private static final long serialVersionUID = 8300520673534294527L;

	@PersistenceContext
	private EntityManager em;

	public List<? extends Customer> findCustomerBy(@NotNull CustomerType type, @NotNull String name) {
		if (type.getCategory().equals(PERSON))
			return findIndividualBy(type, name);
		if (type.getCategory().equals(COMPANY))
			return findOrganizationBy(type, name);
		return Collections.emptyList();
	}

	private List<Individual> findIndividualBy(@NotNull CustomerType type, @NotNull String name) {
		IndividualQuery individualQuery = new IndividualQuery();
		individualQuery.and(individualQuery.customerType().equal(type)).and(individualQuery.byFullName(name));
		return individualQuery.createTypedQuery(em).getResultList();
	}

	private List<Organization> findOrganizationBy(@NotNull CustomerType type, @NotNull String name) {
		OrganizationQuery organizationQuery = new OrganizationQuery();
		organizationQuery.and(organizationQuery.customerType().equal(type)).and(organizationQuery.byName(name));
		return organizationQuery.createTypedQuery(em).getResultList();
	}

}