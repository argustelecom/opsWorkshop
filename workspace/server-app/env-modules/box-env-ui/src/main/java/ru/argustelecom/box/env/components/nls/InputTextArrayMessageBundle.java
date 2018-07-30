package ru.argustelecom.box.env.components.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface InputTextArrayMessageBundle {

	@Message("Каждый элемент массива начинается с новой строки")
	String tooltip();
}
