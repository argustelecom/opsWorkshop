package ru.argustelecom.box.env.task.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.task.nls.TaskMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum TaskType {

	RESOURCES_ACTIVATION,
	RESOURCES_SUSPENSION_FOR_DEBT,
	RESOURCES_SUSPENSION_ON_DEMAND,
	RESOURCES_DEACTIVATION;

	public String getName() {
		TaskMessagesBundle messages = LocaleUtils.getMessages(TaskMessagesBundle.class);

		switch (this) {
			case RESOURCES_ACTIVATION:
				return messages.typeResourceActivation();
			case RESOURCES_SUSPENSION_FOR_DEBT:
				return messages.typeResourceSuspensionForDebt();
			case RESOURCES_SUSPENSION_ON_DEMAND:
				return messages.typeResourceSuspensionOnDemand();
			case RESOURCES_DEACTIVATION:
				return messages.typeResourceDeactivation();
			default:
				throw new SystemException("Unsupported TaskType");
		}
	}
}
