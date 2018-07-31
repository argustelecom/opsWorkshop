package ru.argustelecom.ops.env.party.model.role;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.ops.env.idsequence.IdSequenceService;
import ru.argustelecom.ops.env.security.model.Role;
import ru.argustelecom.ops.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Repository
public class PartyRoleRepository implements Serializable {

	private static final long serialVersionUID = 3766875318830971086L;
	private static final String ALL_EMPLOYEES = "PartyRoleRepository.getAllEmployees";

	public static final long SUPER_USER_ID = 2L;
	public static final long QUEUE_USER_ID = 3L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public void addEmployee(@NotNull Organization organization, @NotNull Employee employee) {
		if (organization.getBinding(employee) == null) {
			OrganizationEmployee newBinding = new OrganizationEmployee(
					idSequence.nextValue(OrganizationEmployee.class));
			newBinding.setOrganization(organization);
			newBinding.setEmployee(employee);
			em.persist(newBinding);
			organization.getEmployeeBindings().add(newBinding);
			em.merge(organization);
		}
	}

	public void removeEmployee(@NotNull Organization organization, @NotNull Employee employee) {
		organization.getEmployeeBindings().remove(organization.getBinding(employee));
		em.merge(organization);
	}

	@NamedQuery(name = ALL_EMPLOYEES, query = "from Employee")
	public List<Employee> getAllEmployees() {
		return em.createNamedQuery(ALL_EMPLOYEES, Employee.class).getResultList();
	}

	private static final String FIND_EMPLOYEES_BY_ROLE = "PartyRoleRepository.findEmployeesByRole";

	@NamedQuery(name = FIND_EMPLOYEES_BY_ROLE, query = "from Employee e where (:role) in e.roles")
	public List<Employee> findEmployeesByRole(Role role) {
		return em.createQuery("from Employee e where :role in elements(e.roles)", Employee.class)
				.setParameter("role", role).getResultList();
	}

	public Employee getSuperUser() {
		return this.em.find(Employee.class, SUPER_USER_ID);
	}

	public Employee getQueueUser() {
		return this.em.find(Employee.class, QUEUE_USER_ID);
	}

}