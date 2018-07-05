package ru.argustelecom.box.nri.logicalresources.nls;


import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface LogicalResourcesMessagesBundle {

	/**
	 * Возвращает сообщение Ошибка
	 *
	 * @return
	 */
	@Message("Ошибка")
	String error();

	/**
	 * Возвращает сообщение Предупреждение
	 *
	 * @return
	 */
	@Message("Предупреждение")
	String warning();

}
