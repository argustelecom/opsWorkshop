package ru.argustelecom.box.env.activity.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface ActivityMessagesBundle {

	@Message(value = "Добавить комментарий")
	String commentCreate();

	@Message(value = "Редактировать комментарий")
	String commentEdit();

}
