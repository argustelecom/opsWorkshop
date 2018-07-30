package ru.argustelecom.ops.env.party.model.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.ops.env.party.model.Company;
import ru.argustelecom.ops.env.party.model.Company_;

@Entity
@Access(AccessType.FIELD)
public class Organization extends Customer {

	private static final long serialVersionUID = 45421002107009361L;

	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrganizationEmployee> employeeBindings = new ArrayList<>();

	protected Organization() {
	}

	public Organization(Long id) {
		super(id);
	}

	public List<Employee> getEmployees() {
		List<Employee> employees = new ArrayList<>(employeeBindings.size());
		employeeBindings.forEach(employeeBinding -> employees.add(employeeBinding.getEmployee()));
		return Collections.unmodifiableList(employees);
	}

	public OrganizationEmployee getBinding(Employee employee) {
		return employeeBindings.stream().filter(binding -> binding.getEmployee().equals(employee)).findFirst()
				.orElse(null);
	}

	protected List<OrganizationEmployee> getEmployeeBindings() {
		return employeeBindings;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class OrganizationQuery extends CustomerQuery<Organization> {

		private Join<Organization, Company> companyJoin;

		public OrganizationQuery() {
			super(Organization.class);
		}

		public Predicate byName(String name) {
			return name == null ? null
					: criteriaBuilder().or(
							criteriaBuilder().like(criteriaBuilder().upper(personJoin().get(Company_.legalName)),
									createParam(Company_.legalName, contains(name))),
							criteriaBuilder().like(criteriaBuilder().upper(personJoin().get(Company_.brandName)),
									createParam(Company_.brandName, contains(name))));
		}

		private Join<Organization, Company> personJoin() {
			if (companyJoin == null)
				companyJoin = root().join(Organization_.party.getName(), JoinType.INNER);
			return companyJoin;
		}

		private String contains(String value) {
			return String.format("%%%s%%", value.toUpperCase().trim());
		}

	}

}