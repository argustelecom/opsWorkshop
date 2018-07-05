package ru.argustelecom.box.env.task.model;

import java.io.Serializable;
import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.task.nls.TaskMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum TaskState implements LifecycleState<TaskState> {

	PENDING("TaskPending", "Pending"),
	RESOLVED("TaskResolved", "Resolved");

	private String eventQualifier;
	private String key;

	@Override
	public Iterable<TaskState> getStates() {
		return Arrays.asList(values());
	}

	@Override
	public String getName() {
		TaskMessagesBundle messages = LocaleUtils.getMessages(TaskMessagesBundle.class);

		switch (this) {
			case PENDING:
				return messages.statePending();
			case RESOLVED:
				return messages.stateResolved();
			default:
				throw new SystemException("Unsupported TaskState");
		}
	}

	@Override
	public String getEventQualifier() {
		return eventQualifier;
	}

	@Override
	public Serializable getKey() {
		return key;
	}
}
