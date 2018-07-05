package ru.argustelecom.box.env.task;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.Getter;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionRoutingService;
import ru.argustelecom.box.env.commodity.lifecycle.ServiceRoutingService;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.env.task.model.TaskState;
import ru.argustelecom.box.env.util.SecurityUtils;
import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.integration.nri.ResourceLoadingService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.exception.SystemException;

@ApplicationService
public class TaskAppService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private LifecycleRoutingService routingService;

	@Inject
	private SubscriptionRoutingService subscriptionRoutingService;

	@Inject
	private SubscriptionRepository subscriptionRepository;

	@Inject
	private ServiceRoutingService serviceRoutingService;

	@Inject
	private ResourceLoadingService rls;

	private static final String GROUPED_EMPLOYEES = "TaskAppService.getGroupedEmployees";

	@NamedQuery(name = GROUPED_EMPLOYEES, query = "select distinct new ru.argustelecom.box.env.task.TaskAppService$RoleEmployee(role, e) "
			+ "from Employee e join e.roles role, Task task " + "where role.id = task.role.id and role in "
			+ "(select role from Employee e join e.roles role where e.id = :employee_id) " + "order by role, e")
	public Map<Role, List<Employee>> getGroupedEmployees() {
		EmployeePrincipal principal = checkNotNull(EmployeePrincipal.instance());
		List<RoleEmployee> groupedEmployee = em.createNamedQuery(GROUPED_EMPLOYEES, RoleEmployee.class)
				.setParameter("employee_id", principal.getEmployeeId()).getResultList();
		Map<Role, List<Employee>> map = new LinkedHashMap<>();
		for (RoleEmployee entry : groupedEmployee) {
			map.computeIfAbsent(entry.getRole(), k -> new ArrayList<>()).add(entry.getEmployee());
		}
		return map;
	}

	public void resolve(Long id) {
		Task task = em.find(Task.class, id);
		routingService.performRouting(task, TaskState.RESOLVED, false);

		em.refresh(task.getSubscription());
		List<Service> services = subscriptionRepository.findServicesBySubscription(task.getSubscription());
		boolean hasServices = services != null && !services.isEmpty();

		switch (task.getTaskType()) {
		case RESOURCES_ACTIVATION:
			if (hasServices) {
				if (SecurityUtils.isNriIntegrationEnabled()) {
					services.forEach(rls::loadResources);
				}
				services.forEach(service -> serviceRoutingService.activate(service));
			}
			break;
		case RESOURCES_DEACTIVATION:
			if (hasServices) {
				services.forEach(service -> serviceRoutingService.deactivate(service));
				if (SecurityUtils.isNriIntegrationEnabled()) {
					services.forEach(rls::releaseLoading);
				}
			}
			subscriptionRoutingService.completeClosure(task.getSubscription());
			break;
		case RESOURCES_SUSPENSION_FOR_DEBT:
			if (hasServices) {
				services.forEach(service -> serviceRoutingService.deactivate(service));
			}
			subscriptionRoutingService.completeSuspensionForDebt(task.getSubscription());
			break;
		case RESOURCES_SUSPENSION_ON_DEMAND:
			if (hasServices) {
				services.forEach(service -> serviceRoutingService.deactivate(service));
			}
			subscriptionRoutingService.completeSuspensionOnDemand(task.getSubscription());
			break;
		default:
			throw new SystemException(String.format("Unsupported task type '%s'", task.getTaskType()));
		}
	}

	public void assignRole(Long taskId, Long roleId) {
		Task task = em.getReference(Task.class, taskId);
		taskRepository.assignRole(task, em.getReference(Role.class, roleId));
	}

	public void assignRole(Collection<Long> taskIdList, Long roleId) {
		Role role = em.getReference(Role.class, roleId);
		getTasks(taskIdList).forEach(task -> taskRepository.assignRole(task, role));
	}

	public void assignEmployee(Long taskId, Long employeeId) {
		Task task = em.getReference(Task.class, taskId);
		taskRepository.assignEmployee(task, em.getReference(Employee.class, employeeId));
	}

	public void assignEmployee(Collection<Long> taskIdList, Long employeeId) {
		Employee employee = em.getReference(Employee.class, employeeId);
		getTasks(taskIdList).forEach(task -> taskRepository.assignEmployee(task, employee));
	}

	private List<Task> getTasks(Collection<Long> taskIdList) {
		return taskIdList.stream().map(taskId -> em.getReference(Task.class, taskId)).collect(Collectors.toList());
	}

	@Getter
	public static class RoleEmployee {
		private Role role;
		private Employee employee;

		public RoleEmployee(Role role, Employee employee) {
			this.role = role;
			this.employee = employee;
		}
	}
}
