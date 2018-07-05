package ru.argustelecom.box.env.task.lifecycle;

import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.env.task.model.TaskState;
import ru.argustelecom.box.env.task.nls.TaskMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@LifecycleRegistrant
public class TaskLifecycle implements LifecycleFactory<TaskState, Task> {
	@Override
	public void buildLifecycle(LifecycleBuilder<TaskState, Task> builder) {
		builder
			.keyword(getClass().getSimpleName())
			.name("Жизненный цикл задачи");

		builder.route(Route.RESOLVE, Route.RESOLVE.getName())
			.from(TaskState.PENDING)
			.to(TaskState.RESOLVED)
			.end()
		.end();
	}

	public enum Route {
		RESOLVE;

		public String getName() {
			TaskMessagesBundle messages = LocaleUtils.getMessages(TaskMessagesBundle.class);

			switch (this) {
				case RESOLVE:
					return messages.routeResolve();
				default:
					throw new SystemException("Unsupported TaskState");
			}
		}
	}
}
