package ru.argustelecom.box.env.party.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface CustomerMessagesBundle {

	@Message("Данные для входа в личный кабинет")
	String personalAreaLoginData();

	@Message("Невозможно отправить квитанцию, так как у клиента не указан адрес для корреспонденции.")
	String customerDoesNotHaveMailEmail();

	// categories
	@Message("Персона")
	String categoryPerson();

	@Message("Организация")
	String categoryCompany();

	@Message("Создание персоны")
	String personCreation();

	@Message("Создание организации")
	String companyCreation();

	@Message("Создание клиента")
	String customerCreation();

}