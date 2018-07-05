package ru.argustelecom.box.env.task;

import static ru.argustelecom.box.env.task.TaskListViewState.TaskFilter.ASSIGNEE;
import static ru.argustelecom.box.env.task.TaskListViewState.TaskFilter.CREATED_FROM;
import static ru.argustelecom.box.env.task.TaskListViewState.TaskFilter.CREATED_TO;
import static ru.argustelecom.box.env.task.TaskListViewState.TaskFilter.NUMBER;
import static ru.argustelecom.box.env.task.TaskListViewState.TaskFilter.TASK_TYPE;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.env.task.model.TaskType;
import ru.argustelecom.system.inf.modelbase.NamedObject;
import ru.argustelecom.system.inf.page.PresentationState;

@Getter
@Setter
@PresentationState
public class TaskListViewState extends FilterViewState implements Serializable {

	@FilterMapEntry(NUMBER)
	private String number;
	@FilterMapEntry(TASK_TYPE)
	private TaskType taskType;
	@FilterMapEntry(value = ASSIGNEE, translator = {AssigneeDtoTranslator.class, RoleDtoTranslator.class})
	private ConvertibleDto assignee;
	@FilterMapEntry(CREATED_FROM)
	private Date createdFrom;
	@FilterMapEntry(CREATED_TO)
	private Date createdTo;

	public static final class TaskFilter {
		public static final String NUMBER = "NUMBER";
		public static final String TASK_TYPE = "TASK_TYPE";
		public static final String ASSIGNEE = "ASSIGNEE";
		public static final String CREATED_FROM = "CREATE_FROM";
		public static final String CREATED_TO = "CREATE_TO";
	}

	private static final long serialVersionUID = -5656436647624209043L;
}