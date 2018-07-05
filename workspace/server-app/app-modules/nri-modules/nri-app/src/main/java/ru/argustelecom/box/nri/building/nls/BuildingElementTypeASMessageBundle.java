package ru.argustelecom.box.nri.building.nls;


import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс лосализации
 */
@MessageBundle(projectCode = "")
public interface BuildingElementTypeASMessageBundle {

	/**
	 * Возвращает сообщение Невозможно удалить тип
	 *
	 * @return
	 */
	@Message("Невозможно удалить тип ")
	String canNotDeleteType();

	/**
	 * Возвращает сообщение , т.к. в системе существуют элементы строения такого типа.
	 *
	 * @return
	 */
	@Message(", т.к. в системе существуют элементы строения такого типа.")
	String because();
}
