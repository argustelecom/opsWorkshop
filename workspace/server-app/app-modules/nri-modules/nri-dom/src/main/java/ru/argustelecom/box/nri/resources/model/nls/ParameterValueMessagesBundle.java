package ru.argustelecom.box.nri.resources.model.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface ParameterValueMessagesBundle {

	/**
	 * возвращает строку Нельзя выставлять значение переменной, у которой не указана спецификация
	 *
	 * @return
	 */
	@Message("Нельзя выставлять значение переменной, у которой не указана спецификация")
	String specificationIsNull();

	/**
	 * возвращает строку Значение параметра невалидно согласно спецификации
	 *
	 * @return
	 */
	@Message("Значение параметра невалидно согласно спецификации")
	String invalidParameter();
}
