package ru.argustelecom.ops.env.party.model.role;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.ops.inf.modelbase.BusinessObject;

@Entity
@Table(schema = "system", name = "organization_employee", uniqueConstraints = {
		@UniqueConstraint(name = "uc_organization_employee", columnNames = { "organization_id", "employee_id" }) })
@Access(AccessType.FIELD)
public class OrganizationEmployee extends BusinessObject {

	private static final long serialVersionUID = 5101092061849152609L;

	@ManyToOne(fetch = FetchType.LAZY)
	private Organization organization;

	@ManyToOne(fetch = FetchType.LAZY)
	private Employee employee;

	protected OrganizationEmployee() {
	}

	public OrganizationEmployee(Long id) {
		super(id);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}