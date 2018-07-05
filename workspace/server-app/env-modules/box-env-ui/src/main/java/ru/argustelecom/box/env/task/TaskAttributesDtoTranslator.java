package ru.argustelecom.box.env.task;

import javax.inject.Inject;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class TaskAttributesDtoTranslator implements DefaultDtoTranslator<TaskAttributesDto, Task> {

	@Inject
	private RoleDtoTranslator roleDtoTranslator;

	@Inject
	private AssigneeDtoTranslator assigneeDtoTranslator;

	@Override
	public TaskAttributesDto translate(Task task) {
		//@formatter:off
				return TaskAttributesDto.builder()
						.id(task.getId())
						.number(task.getNumber())
						.taskType(task.getTaskType())
						.role(task.getRole() != null ? roleDtoTranslator.translate(task.getRole()) : null)
						.assignee(task.getAssignee() != null ? assigneeDtoTranslator.translate(task.getAssignee()) : null)
						.createDateTime(task.getCreateDateTime())
						.state(task.getState())
						.build();
		//@formatter:on
	}

}
