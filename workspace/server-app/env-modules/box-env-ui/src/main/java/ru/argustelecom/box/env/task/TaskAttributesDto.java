package ru.argustelecom.box.env.task;

import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.env.task.model.TaskState;
import ru.argustelecom.box.env.task.model.TaskType;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class TaskAttributesDto extends ConvertibleDto {

	private Long id;
	private String number;
	private TaskType taskType;
	private RoleDto role;
	private AssigneeDto assignee;
	private Date createDateTime;
	private TaskState state;

	@Builder
	public TaskAttributesDto(Long id, String number, TaskType taskType, RoleDto role, AssigneeDto assignee,
			Date createDateTime, TaskState state) {
		this.id = id;
		this.number = number;
		this.taskType = taskType;
		this.role = role;
		this.assignee = assignee;
		this.createDateTime = createDateTime;
		this.state = state;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Task.class;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return TaskAttributesDtoTranslator.class;
	}

}
