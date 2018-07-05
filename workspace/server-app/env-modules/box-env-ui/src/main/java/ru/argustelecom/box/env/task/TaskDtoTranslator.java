package ru.argustelecom.box.env.task;

import javax.inject.Inject;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class TaskDtoTranslator implements DefaultDtoTranslator<TaskDto, Task> {

	@Inject
	private AssigneeDtoTranslator assigneeDtoTranslator;

	@Inject
	private RoleDtoTranslator roleDtoTranslator;

	@Inject
	private TaskInfoDtoTranslator taskInfoDtoTranslator;

	public TaskDto translate(Task task) {
		//@formatter:off
		return TaskDto.builder()
				.id(task.getId())
				.number(task.getNumber())
				.taskType(task.getTaskType())
				.roleDto(task.getRole() != null ? roleDtoTranslator.translate(task.getRole()) : null)
				.assigneeDto(task.getAssignee() != null ? assigneeDtoTranslator.translate(task.getAssignee()) : null)
				.taskInfoDto(taskInfoDtoTranslator.translate(task))
				.createDateTime(task.getCreateDateTime())
				.comment(task.getComment())
				.state(task.getState())
				.build();
		//@formatter:on
	}
}