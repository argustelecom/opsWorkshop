package ru.argustelecom.box.env.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.order.nls.OrderMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.NamedObject;

@AllArgsConstructor
public enum OrderPriority implements NamedObject {

	//@formatter:off
	LOW 	("blue"),
	MIDDLE 	("green"),
	HIGH 	("red");
	//@formatter:off

	@Getter
	private String color;

	public String getName() {
		OrderMessagesBundle messages = LocaleUtils.getMessages(OrderMessagesBundle.class);

		switch (this) {
			case LOW:
				return messages.priorityLow();
			case MIDDLE:
				return messages.priorityMiddle();
			case HIGH:
				return messages.priorityHigh();
			default:
				throw new SystemException("Unsupported OrderPriority");
		}
	}

	@Override
	public String getObjectName() {
		return getName();
	}
}