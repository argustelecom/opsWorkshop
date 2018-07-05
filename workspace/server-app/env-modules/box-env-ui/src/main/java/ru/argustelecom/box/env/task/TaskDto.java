package ru.argustelecom.box.env.task;

import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.env.task.model.TaskState;
import ru.argustelecom.box.env.task.model.TaskType;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class TaskDto extends ConvertibleDto {

	private Long id;
	private String number;
	private TaskType taskType;
	private TaskInfoDto taskInfoDto;
	private RoleDto role;
	private AssigneeDto assignee;
	private Date createDateTime;
	private Date dueDateTime;
	private String comment;
	private TaskState state;

	//@formatter:off
	@Builder
	public TaskDto(Long id,
				   String number,
				   TaskType taskType,
				   TaskInfoDto taskInfoDto,
				   RoleDto roleDto,
				   AssigneeDto assigneeDto,
				   Date createDateTime,
				   Date dueDateTime,
				   String comment,
				   TaskState state) {
		this.id = id;
		this.number = number;
		this.taskType = taskType;
		this.taskInfoDto = taskInfoDto;
		this.role = roleDto;
		this.assignee = assigneeDto;
		this.createDateTime = createDateTime;
		this.dueDateTime = dueDateTime;
		this.comment = comment;
		this.state = state;
	}
	//@formatter:on

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return TaskDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Task.class;
	}
}