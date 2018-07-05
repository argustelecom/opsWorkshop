package ru.argustelecom.box.env.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.beust.jcommander.internal.Lists;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.login.LoginService;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class TaskAssigneeDialogModel implements Serializable {

	private static final long serialVersionUID = 3977319644116543910L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TaskAppService taskAppService;

	@Inject
	private LoginService loginService;

	@Inject
	private AssigneeDtoTranslator assigneeDtoTranslator;

	@Inject
	private RoleDtoTranslator roleDtoTranslator;

	@Getter
	private List<TaskDto> selectedTasks;
	private List<RoleDto> selectedRoles;

	@Getter
	@Setter
	private Callback<List<TaskDto>> callback;

	@Getter
	@Setter
	private ConvertibleDto assignee;
	private Map<RoleDto, List<SelectItem>> groupedAssignees;
	@Getter
	private AssigneeDto currentEmployee;

	@PostConstruct
	private void init() {
		currentEmployee = assigneeDtoTranslator.translate(loginService.getCurrentEmployee());
		createGroupedEmployees();
	}

	public void onAssign() {
		List<Long> taskIdList = selectedTasks.stream().map(TaskDto::getId).collect(Collectors.toList());
		if (assignee instanceof RoleDto) {
			taskAppService.assignRole(taskIdList, assignee.getId());
		}
		if (assignee instanceof AssigneeDto) {
			taskAppService.assignEmployee(taskIdList, assignee.getId());
		}

		callback.execute(selectedTasks);
	}

	public List<SelectItem> getAssignees() {
		List<SelectItem> result = new ArrayList<>();

		result.addAll(groupedAssignees.get(selectedRoles.get(0)));
		selectedRoles.stream().skip(0).forEach(selectedRole -> {
			result.removeIf(selectItem -> groupedAssignees.get(selectedRole).stream()
					.noneMatch(otherSelectItem -> otherSelectItem.getValue().equals(selectItem.getValue())));
		});

		return result;
	}

	public String formatRoles() {
		return selectedRoles.stream().map(RoleDto::getName).collect(Collectors.joining(", "));
	}

	public void setSelectedTasks(List<TaskDto> selectedTasks) {
		this.selectedTasks = selectedTasks;
		this.selectedRoles = selectedTasks.stream().map(TaskDto::getRole).distinct().collect(Collectors.toList());
		assignee = null;
	}

	public void setSelectedTask(TaskDto selectedTask) {
		setSelectedTasks(Lists.newArrayList(selectedTask));
	}

	private void createGroupedEmployees() {
		groupedAssignees = new HashMap<>();
		taskAppService.getGroupedEmployees().forEach((role, employees) -> {
			List<SelectItem> assignees = new ArrayList<>();
			RoleDto roleDto = roleDtoTranslator.translate(role);
			assignees.add(new SelectItem(roleDto, roleDto.getName()));
			for (Employee employee : employees) {
				AssigneeDto assigneeDto = assigneeDtoTranslator.translate(employee);
				assignees.add(new SelectItem(assigneeDto, assigneeDto.getName()));
			}
			groupedAssignees.put(roleDto, assignees);
		});
	}
}