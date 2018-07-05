package ru.argustelecom.box.env.message.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface MessageMessagesBundle {

	@Message("Ошибка при создании сообщения")
	String createMessageError();

	@Message("Ошибка при отправке письма")
	String sendMessageError();

}
