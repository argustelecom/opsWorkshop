package ru.argustelecom.box.env.task;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.task.model.TaskType;
import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class TaskListViewModel extends ViewModel {

	private static final long serialVersionUID = 1334209033719921477L;

	public static final String VIEW_ID = "/views/env/task/TaskListView.xhtml";

	@Inject
	private TaskAppService taskAppService;

	@Inject
	@Getter
	private TaskLazyDataModel lazyDm;

	@Inject
	private RoleDtoTranslator roleDtoTranslator;

	@Inject
	private AssigneeDtoTranslator assigneeDtoTranslator;

	@Getter
	private List<SelectItem> assignees = new ArrayList<>();

	private EmployeePrincipal employeePrincipal;

	@Getter
	@Setter
	private List<TaskDto> selectedTasks = new ArrayList<>();

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		createAssigneesList();
		employeePrincipal = checkNotNull(EmployeePrincipal.instance());
	}

	public String formatAddresses(TaskDto task) {
		return task.getTaskInfoDto().getAddresses().stream().collect(Collectors.joining(",\n"));
	}

	public TaskType[] getTaskTypes() {
		return TaskType.values();
	}

	public Callback<List<TaskDto>> getAssignCallback() {
		return (selectedTasks -> lazyDm.reloadData());
	}

	public Callback<TaskDto> getResolveCallback() {
		return (selectedTask -> lazyDm.reloadData());
	}

	public boolean canAssign() {
		return !selectedTasks.isEmpty();
	}

	public boolean canResolve(TaskDto task) {
		if (task.getAssignee() == null) {
			return false;
		}
		if (!task.getAssignee().getId().equals(employeePrincipal.getEmployeeId())) {
			return false;
		}

		return true;
	}

	private void createAssigneesList() {
		taskAppService.getGroupedEmployees().forEach((role, employees) -> {
			RoleDto roleDto = roleDtoTranslator.translate(role);
			assignees.add(new SelectItem(roleDto, roleDto.getName()));
			for (Employee employee : employees) {
				AssigneeDto assigneeDto = assigneeDtoTranslator.translate(employee);
				assignees.add(new SelectItem(assigneeDto, assigneeDto.getName()));
			}
		});
	}
}