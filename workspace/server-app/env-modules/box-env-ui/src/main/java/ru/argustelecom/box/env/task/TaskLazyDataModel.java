package ru.argustelecom.box.env.task;

import static ru.argustelecom.box.env.task.TaskLazyDataModel.TaskSort;
import static ru.argustelecom.box.env.task.model.Task.TaskQuery;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.criteria.JoinType;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.Party_;
import ru.argustelecom.box.env.party.model.role.Employee_;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.env.task.model.Task_;

public class TaskLazyDataModel
		extends EQConvertibleDtoLazyDataModel<Task, TaskDto, TaskQuery, TaskSort> {

	@Inject
	private TaskListFilterModel taskListFilterModel;

	@Inject
	private TaskDtoTranslator taskDtoTranslator;

	@PostConstruct
	private void postConstruct() {
		initPaths();
	}

	private void initPaths() {
		addPath(TaskSort.number, query -> query.root().get(Task_.number));
		addPath(TaskSort.taskType, query -> query.root().get(Task_.taskType));
		addPath(TaskSort.createDateTime, query -> query.root().get(Task_.createDateTime));
		addPath(TaskSort.assignee, query -> query.root().join(Task_.assignee, JoinType.LEFT)
				.join(Employee_.party, JoinType.LEFT).get(Party_.sortName));
	}

	@Override
	protected Class<TaskSort> getSortableEnum() {
		return TaskSort.class;
	}

	@Override
	protected DefaultDtoTranslator<TaskDto, Task> getDtoTranslator() {
		return taskDtoTranslator;
	}

	@Override
	protected EQConvertibleDtoFilterModel<TaskQuery> getFilterModel() {
		return taskListFilterModel;
	}

	public enum TaskSort {
		number, createDateTime, assignee, taskType
	}

	private static final long serialVersionUID = -4576568204376937836L;
}
