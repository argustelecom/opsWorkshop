package ru.argustelecom.box.nri.resources.inst.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface ResourceInstanceAppServiceMessagesBundle {


	/**
	 * возвращает строку Спецификация данного параметра не была найдена
	 * @return
	 */
	@Message("Спецификация данного параметра не была найдена")
	String specWasNotFound();
}
