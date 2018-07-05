package ru.argustelecom.box.env.commodity.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface ServiceMessagesBundle {

	@Message(value = "Ресурсы")
	String listOfResources();

	// states
	@Message("Активна")
	String stateActive();

	@Message("Неактивна")
	String stateInactive();

	@Message("У опции услуги '%s' не указан тариф")
	String serviceOptionNotSpecifyTariff(String name);

	@Message("В тарифе, заданном для опции '%s' найдены префиксы, используемые в других тарифах услуги")
	String intersectedPrefixesExistInTariffs(String name);

}
