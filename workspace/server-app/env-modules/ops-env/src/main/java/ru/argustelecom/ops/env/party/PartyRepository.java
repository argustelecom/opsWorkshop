package ru.argustelecom.ops.env.party;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import ru.argustelecom.ops.env.contact.ContactInfo;
import ru.argustelecom.ops.env.idsequence.IdSequenceService;
import ru.argustelecom.ops.env.login.model.Login;
import ru.argustelecom.ops.env.party.model.PartyRole;
import ru.argustelecom.ops.env.party.model.Person;
import ru.argustelecom.ops.env.party.model.PersonName;
import ru.argustelecom.ops.env.party.model.role.Employee;
import ru.argustelecom.ops.env.util.QueryWrapper;
import ru.argustelecom.ops.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.login.ILoginService;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Repository
public class PartyRepository implements Serializable {

	private static final long serialVersionUID = 8715501239266090859L;

	private static final String GET_ALL_EMPLOYEES_INFO = "PartyRepository.getAllEmployeesInfo";
	private static final String GET_SEARCH_EMPLOYEES_INFO = "PartyRepository.getSearchEmployeesInfo";
	private static final String SEARCH_QUERY_PARAM = "searchQuery";
	private static final String ALL_CUSTOMERS = "PartyRepository.getAllCustomers";
	private static final String FIND_EMPLOYEE_BY_PERSONNEL_NUMBER = "PartyRepository.findEmployeeByPersonnelNumber";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private ILoginService loginService;

	@NamedQuery(
			name = FIND_EMPLOYEE_BY_PERSONNEL_NUMBER,
			query = "select e from Employee e where e.personnelNumber = :personnelNumber")
	public Employee findEmployeeByPersonnelNumber(String personnelNumber) {
		checkArgument(StringUtils.isNotBlank(personnelNumber));
		try {
			return em.createNamedQuery(FIND_EMPLOYEE_BY_PERSONNEL_NUMBER, Employee.class)
					.setParameter("personnelNumber", personnelNumber).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public Employee createEmployee(String prefix, String lastName, String firstName, String secondName, String suffix,
			String personnelNumber, String note, ContactInfo contactInfo) {

		checkArgument(StringUtils.isNotBlank(lastName));
		checkArgument(StringUtils.isNotBlank(firstName));
		checkArgument(StringUtils.isNotBlank(personnelNumber));

		Employee newEmployee = new Employee(idSequence.nextValue(Employee.class));
		newEmployee.setPersonnelNumber(personnelNumber);
		newEmployee.setFired(false);

		createPerson(prefix, lastName, firstName, secondName, suffix, note, contactInfo, newEmployee);

		em.persist(newEmployee);
		em.flush();
		return newEmployee;
	}

	public void fireEmployee(@NotNull Employee employee) {
		Login login = null;
		try {
			login = em.createNamedQuery(Login.FIND_LOGIN_BY_EMPLOYEE, Login.class).setParameter("employee", employee)
					.getSingleResult();
		} catch (NoResultException ignored) {
		}
		if (login != null) {
			loginService.lockLogin(login);
		}
		employee.setFired(true);
		em.merge(employee);
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private Person createPerson(String prefix, String lastName, String firstName, String secondName, String suffix,
			String note, ContactInfo contactInfo, PartyRole partyRole) {

		checkArgument(StringUtils.isNotBlank(lastName));
		checkArgument(StringUtils.isNotBlank(firstName));
		checkNotNull(partyRole);

		//@formatter:off
		Person newPerson = Person.builder().id(idSequence.nextValue(Person.class)).name(PersonName.of(prefix, firstName, secondName, lastName, suffix)).note(note).build();
		//@formatter:on

		newPerson.addRole(partyRole);
		partyRole.setParty(partyRole.getParty());

		if (contactInfo != null) {
			newPerson.setContactInfo(contactInfo);
			contactInfo.getContacts().forEach(em::persist);
		}

		em.persist(newPerson);
		return newPerson;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class LoginEmployeeQueryWrapper extends QueryWrapper<LoginEmployee> {

		public static final String ID = "e.id";
		public static final String USER_NAME = "l.username";
		public static final String PERSONNEL_NUMBER = "e.personnelNumber";
		public static final String LAST_NAME = "e.party.name.lastName";
		public static final String FIRST_NAME = "e.party.name.firstName";
		public static final String SECOND_NAME = "e.party.name.secondName";
		public static final String EMAIL = "l.email";

		private static final String SELECT = "new ru.argustelecom.ops.env.party.PartyRepository$LoginEmployee(l, e)";
		private static final String FROM = "Login l right outer join l.employee as e";

		public LoginEmployeeQueryWrapper() {
			super(LoginEmployee.class, SELECT, FROM);
		}

	}

	@Getter
	public static class LoginEmployee implements Identifiable {
		private Long id;
		private Login login;
		private Employee employee;

		public LoginEmployee(Long id) {
			this.id = id;
		}

		public LoginEmployee(Login login, Employee employee) {
			this.login = login;
			this.employee = employee;
			this.id = employee.getId();
		}
	}

}