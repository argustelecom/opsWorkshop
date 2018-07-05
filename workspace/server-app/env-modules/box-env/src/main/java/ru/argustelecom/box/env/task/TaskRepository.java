package ru.argustelecom.box.env.task;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.login.LoginService;
import ru.argustelecom.box.env.numerationpattern.NumberGenerator;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.env.task.model.Task.TaskQuery;
import ru.argustelecom.box.env.task.model.TaskState;
import ru.argustelecom.box.env.task.model.TaskType;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class TaskRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService sequenceService;

	@Inject
	private NumberGenerator numberGenerator;

	@Inject
	private LoginService loginService;

	public List<Task> findActiveTasks() {
		Employee employee = loginService.getCurrentEmployee();
		return em.createQuery("from Task where role in :roles and state = :state", Task.class)
				.setParameter("roles", employee.getRoles())
				.setParameter("state", TaskState.PENDING)
				.getResultList();
	}

	public Long getActiveTasksCount() {
		TaskQuery query = new TaskQuery();
		return query.and(query.byState(TaskState.PENDING)).calcRowsCount(em);
	}

	public Task create(Employee employee, Role role, String comment, Subscription subscription, Date createDateTime,
			TaskType taskType) {
		Task task = new Task(sequenceService.nextValue(Task.class));
		task.setNumber(numberGenerator.generateNumber(Task.class));
		task.setAssignee(employee);
		task.setRole(role);
		task.setComment(comment);
		task.setSubscription(subscription);
		task.setCreateDateTime(createDateTime);
		task.setTaskType(taskType);
		task.setState(TaskState.PENDING);
		em.persist(task);
		return task;
	}

	public void assignRole(Task task, Role role) {
		task.setRole(role);
		task.setAssignee(null);
	}

	public void assignEmployee(Task task, Employee employee) {
		task.setAssignee(employee);
	}

	private static final long serialVersionUID = -8448244000974853035L;

}
