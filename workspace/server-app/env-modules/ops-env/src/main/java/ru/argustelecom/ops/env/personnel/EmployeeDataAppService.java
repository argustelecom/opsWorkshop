package ru.argustelecom.ops.env.personnel;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Objects;

import ru.argustelecom.ops.env.party.PartyRepository;
import ru.argustelecom.ops.env.party.model.role.Employee;
import ru.argustelecom.ops.env.person.PersonDataAppService;
import ru.argustelecom.ops.inf.security.SecurityContext;
import ru.argustelecom.ops.inf.service.ApplicationService;

@ApplicationService
public class EmployeeDataAppService implements Serializable {

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private PersonDataAppService pdAddService;

	@Inject
	private PartyRepository partyRepository;

	@Inject
	private SecurityContext security;

	public void renamePerson(Long personId, String prefix, String firstName, String secondName, String lastName,
			String suffix) {
		security.checkGranted("System_EmployeeEdit");
		pdAddService.renamePerson(personId, prefix, firstName, secondName, lastName, suffix);
	}

	public void editPersonData(Long personId, String note) {
		security.checkGranted("System_EmployeeEdit");
		pdAddService.editPersonData(personId, note);
	}

	public Employee createEmployee(String prefix, String firstName, String secondName, String lastName, String suffix,
			String personnelNumber, String note) {
		security.checkGranted("System_EmployeeEdit");
		return partyRepository.createEmployee(prefix, lastName, firstName, secondName, suffix,
				personnelNumber, note, null);
	}

	public void editEmployeeData(Long employeeId, String personnelNumber) {
		checkArgument(employeeId != null, "employeeId is required");
		checkArgument(StringUtils.isNotBlank(personnelNumber), "personnel number is required");
		security.checkGranted("System_EmployeeEdit");

		Employee employee = em.find(Employee.class, employeeId);

		if (!Objects.equal(employee.getPersonnelNumber(), personnelNumber)) {
			employee.setPersonnelNumber(personnelNumber);
		}
	}

	public void fireEmployee(Long employeeId) {
		checkNotNull(employeeId, "employeeId is required");
		security.checkGranted("System_DeleteEmployee");

		Employee employee = em.find(Employee.class, employeeId);
		partyRepository.fireEmployee(employee);
	}

	public Employee findEmployeeByPersonnelNumber(String personnelNumber) {
		checkArgument(StringUtils.isNotBlank(personnelNumber), "personnel number is required");
		security.checkGranted("System_PersonalView");

		return partyRepository.findEmployeeByPersonnelNumber(personnelNumber);
	}

	private static final long serialVersionUID = -4245022772446933639L;
}