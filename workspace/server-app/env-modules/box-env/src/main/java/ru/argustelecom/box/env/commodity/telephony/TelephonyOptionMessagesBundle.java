package ru.argustelecom.box.env.commodity.telephony;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface TelephonyOptionMessagesBundle {

	@Message("Активна")
	String stateActive();

	@Message("Неактивна")
	String stateInactive();

}