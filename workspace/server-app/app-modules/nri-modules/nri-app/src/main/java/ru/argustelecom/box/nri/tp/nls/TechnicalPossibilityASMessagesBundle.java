package ru.argustelecom.box.nri.tp.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface TechnicalPossibilityASMessagesBundle {
	/**
	 * Возвращает строку Не передан адрес для проверки тех.возможности
	 * @return
	 */
	@Message("Не передан адрес для проверки тех.возможности")
	String addressIsNull();

	/**
	 * Возвращает строку Не указана спецификация услуги для проверки ТВ
	 * @return
	 */
	@Message("Не указана спецификация услуги для проверки ТВ")
	String serviceSpecIsNull();
}
