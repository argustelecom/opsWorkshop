package ru.argustelecom.ops.env.party.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface PartyMessagesBundle {

	@Message("Персона")
	String categoryPerson();

	@Message("Имя компании должно быть уникальным")
	String companyNameShouldBeUnique();
}
