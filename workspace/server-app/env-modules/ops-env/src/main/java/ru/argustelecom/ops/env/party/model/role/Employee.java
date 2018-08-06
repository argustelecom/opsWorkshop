package ru.argustelecom.ops.env.party.model.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import ru.argustelecom.ops.env.party.model.PartyRole;
import ru.argustelecom.ops.env.party.model.Person;
import ru.argustelecom.ops.env.party.model.PersonName;
import ru.argustelecom.ops.env.security.model.Role;

/**
 * Роль описывающая {@linkplain ru.argustelecom.ops.env.party.model.Party участника} как работника.
 */
@Entity
@Access(AccessType.FIELD)
public class Employee extends PartyRole {

	private static final long serialVersionUID = -4206745377739673861L;

	@Column(nullable = false, length = 64)
	private String personnelNumber;

	@Column(nullable = false)
	private Boolean fired;

	//@formatter:off
	@ManyToMany
	@JoinTable(schema = "system", name = "employee_roles",
			joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private List<Role> roles = new ArrayList<>();
	//@formatter:on

	protected Employee() {
	}

	public Employee(Long id) {
		super(id);
	}

	@Transient
	private Person person;

	@Override
	public String getObjectName() {
		return getParty().getObjectName();
	}

	public void addRole(Role role) {
		roles.add(role);
	}

	public void removeRole(Role role) {
		roles.remove(role);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public String getPersonnelNumber() {
		return personnelNumber;
	}

	public void setPersonnelNumber(String personnelNumber) {
		this.personnelNumber = personnelNumber;
	}

	public Boolean getFired() {
		return fired;
	}

	public void setFired(Boolean fired) {
		this.fired = fired;
	}

	public List<Role> getRoles() {
		return Collections.unmodifiableList(roles);
	}

	public Employee(Long employeeId, String employeeName, String personnelNumber,
			boolean fired, Long personId, String prefix, String firstName, String secondName, String lastName,
			String suffix, String note) {

		this.person = Person.builder().id(personId).name(PersonName.of(prefix, firstName, secondName, lastName, suffix))
				.note(note).build();

		this.id = employeeId;
		this.objectName = employeeName;
		this.personnelNumber = personnelNumber;
		this.fired = fired;
	}

	public Person getPerson() {
		return person;
	}
}