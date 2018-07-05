package ru.argustelecom.box.nri.service.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface CreateRequirementDMMessagesBundle {

	/**
	 * возвращает строку Не удалось создать требование
	 * @return
	 */
	@Message("Не удалось создать требование")
	String couldNotCreateRequirement();
}
