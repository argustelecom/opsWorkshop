package ru.argustelecom.box.nri.building.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;


/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface BuildingElementTypeVMMessagesBundle {


	/**
	 * Возвращает выражение Ошибка
	 * @return
	 */
	@Message("Ошибка")
	String error();
	/**
	 * Возвращает выражение Имя не уникально
	 * @return
	 */
	@Message("Имя не уникально")
	String nameDoesNotUnique();


	/**
	 * Возвращает выражение Имя не должно быть пустым
	 * @return
	 */
	@Message("Имя не должно быть пустым")
	String nameCanNotBeEmpty();


}
