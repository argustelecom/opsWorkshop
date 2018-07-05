package ru.argustelecom.box.env.task;

import java.io.Serializable;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class TaskLifecycleDialogModel implements Serializable {
	private static final long serialVersionUID = -5518160115311223622L;

	@Inject
	private TaskAppService taskAppService;

	@Setter
	private TaskDto selectedTask;

	@Getter
	@Setter
	private Callback<TaskDto> callback;

	public void onResolve() {
		taskAppService.resolve(selectedTask.getId());
		callback.execute(selectedTask);
	}
}