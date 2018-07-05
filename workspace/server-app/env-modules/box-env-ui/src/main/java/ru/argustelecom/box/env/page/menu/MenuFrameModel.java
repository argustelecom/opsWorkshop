package ru.argustelecom.box.env.page.menu;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.task.TaskRepository;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "menuFm")
@PresentationModel
public class MenuFrameModel implements Serializable {

	@Inject
	private TaskRepository taskRp;

	private Long taskCount;

	public Long getTaskCount() {
		if (taskCount == null)
			taskCount = taskRp.getActiveTasksCount();
		return taskCount;
	}

	private static final long serialVersionUID = 7863029907538990699L;

}