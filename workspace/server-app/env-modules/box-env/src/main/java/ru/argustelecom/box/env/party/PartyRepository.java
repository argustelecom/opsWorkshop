package ru.argustelecom.box.env.party;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import ru.argustelecom.box.env.contact.ContactInfo;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.login.model.Login;
import ru.argustelecom.box.env.party.model.Appointment;
import ru.argustelecom.box.env.party.model.Company;
import ru.argustelecom.box.env.party.model.Company.CompanyQuery;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.PersonName;
import ru.argustelecom.box.env.party.model.role.ContactPerson;
import ru.argustelecom.box.env.party.model.role.ContactPerson.ContactPersonQuery;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.util.QueryWrapper;
import ru.argustelecom.box.inf.service.Repository;
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

	public ContactPerson createContactPerson(Company company, String prefix, String lastName, String firstName,
			String secondName, String suffix, String appointment, ContactInfo contactInfo) {
		checkArgument(company != null, "Company is required");

		//@formatter:off
		ContactPerson newContactPerson = ContactPerson.builder()
			.id(idSequence.nextValue(ContactPerson.class)) 
			.company(company)
			.appointment(appointment)
		.build();	
		//@formatter:on

		createPerson(prefix, lastName, firstName, secondName, suffix, null, contactInfo, newContactPerson);

		em.persist(newContactPerson);

		company.getContactPersons().add(newContactPerson);

		return newContactPerson;
	}

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
			String personnelNumber, Appointment appointment, String note, ContactInfo contactInfo) {

		checkArgument(StringUtils.isNotBlank(lastName));
		checkArgument(StringUtils.isNotBlank(firstName));
		checkArgument(StringUtils.isNotBlank(personnelNumber));

		Employee newEmployee = new Employee(idSequence.nextValue(Employee.class));
		newEmployee.setPersonnelNumber(personnelNumber);
		newEmployee.setAppointment(appointment);
		newEmployee.setFired(false);

		createPerson(prefix, lastName, firstName, secondName, suffix, note, contactInfo, newEmployee);

		em.persist(newEmployee);
		em.flush();
		return newEmployee;
	}

	public List<ContactPerson> getCompanyContactPersons(Company company) {
		if (company == null) {
			return Collections.emptyList();
		}
		ContactPersonQuery query = new ContactPersonQuery();
		return query.and(query.company().equal(company)).getResultList(em);
	}

	public void removeContactPerson(ContactPerson contactPerson) {
		checkArgument(contactPerson != null);
		contactPerson.getCompany().getContactPersons().remove(contactPerson);
		em.remove(contactPerson);
		em.remove(contactPerson.getParty());
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

	public boolean hasCompanyWith(String legalName) {
		checkNotNull(legalName);

		CompanyQuery companyQuery = new CompanyQuery();
		return !companyQuery.and(companyQuery.legalName().equal(legalName)).getResultList(em).isEmpty();
	}

	Company createCompany(@NotNull String legalName, String brandName, ContactInfo contactInfo, PartyRole partyRole) {
		Company newCompany = new Company(idSequence.nextValue(Company.class));
		newCompany.setLegalName(legalName);
		newCompany.setBrandName(brandName);

		newCompany.addRole(partyRole);
		partyRole.setParty(newCompany);

		if (contactInfo != null) {
			newCompany.setContactInfo(contactInfo);
			contactInfo.getContacts().forEach(em::persist);
		}

		em.persist(newCompany);
		return newCompany;
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
		Person newPerson = Person.builder()
			.id(idSequence.nextValue(Person.class))
			.name(PersonName.of(prefix, firstName, secondName, lastName, suffix))
			.note(note)
		.build();
		//@formatter:on		

		newPerson.addRole(partyRole);
		partyRole.setParty(newPerson);

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

		private static final String SELECT = "new ru.argustelecom.box.env.party.PartyRepository$LoginEmployee(l, e)";
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