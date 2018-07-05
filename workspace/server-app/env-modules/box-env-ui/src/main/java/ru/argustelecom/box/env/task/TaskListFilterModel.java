package ru.argustelecom.box.env.task;

import static ru.argustelecom.box.env.task.TaskListViewState.TaskFilter.ASSIGNEE;
import static ru.argustelecom.box.env.task.TaskListViewState.TaskFilter.CREATED_FROM;
import static ru.argustelecom.box.env.task.TaskListViewState.TaskFilter.CREATED_TO;
import static ru.argustelecom.box.env.task.TaskListViewState.TaskFilter.NUMBER;
import static ru.argustelecom.box.env.task.TaskListViewState.TaskFilter.TASK_TYPE;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.env.task.model.Task.TaskQuery;
import ru.argustelecom.box.env.task.model.TaskState;
import ru.argustelecom.box.env.task.model.TaskType;

public class TaskListFilterModel extends BaseEQConvertibleDtoFilterModel<TaskQuery> {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TaskListViewState taskListViewState;

	@Override
	public void buildPredicates(TaskQuery query) {
		Map<String, Object> filterMap = taskListViewState.getFilterMap();
		for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
			switch (entry.getKey()) {
			case NUMBER:
				addPredicate(query.byNumber((String) entry.getValue()));
				break;
			case TASK_TYPE:
				addPredicate(query.byTaskType((TaskType) entry.getValue()));
				break;
			case CREATED_FROM:
				addPredicate(query.byFromCreationDate((Date) entry.getValue()));
				break;
			case CREATED_TO:
				addPredicate(query.byToCreationDate((Date) entry.getValue()));
				break;
			case ASSIGNEE: {
				if (entry.getValue() instanceof RoleDto) {
					addPredicate(query.byRole((Role) ((RoleDto) entry.getValue()).getIdentifiable(em)));
					addPredicate(query.byEmployeeIsNull());
				}
				if (entry.getValue() instanceof AssigneeDto) {
					addPredicate(query.byEmployee((Employee) ((AssigneeDto) entry.getValue()).getIdentifiable(em)));
				}
				break;
			}
			default:
				break;
			}
		}
		addPredicate(query.byState(TaskState.PENDING));
	}

	@Override
	public Supplier<TaskQuery> entityQuerySupplier() {
		return TaskQuery::new;
	}

	private static final long serialVersionUID = -7591609223754162456L;
}
