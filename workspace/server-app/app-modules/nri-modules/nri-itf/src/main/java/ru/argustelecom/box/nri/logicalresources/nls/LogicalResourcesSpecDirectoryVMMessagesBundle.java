package ru.argustelecom.box.nri.logicalresources.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface LogicalResourcesSpecDirectoryVMMessagesBundle {

	/**
	 * возвращает строку Существующие данные ссылаются на текущую запись, удаление невозможно
	 * @return
	 */
	@Message("Существующие данные ссылаются на текущую запись, удаление невозможно")
	String canNot();
}
