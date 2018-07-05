package ru.argustelecom.box.env.party.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface PartyMessagesBundle {

	@Message("Персона")
	String categoryPerson();

	@Message("Организация")
	String categoryCompany();

	@Message("Имя компании должно быть уникальным")
	String companyNameShouldBeUnique();
}
