package ru.argustelecom.box.nri.building.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface ResourceInstallationVMMessagesBundle {

	/**
	 * Возвращает сообщение Не указана инсталляция
	 *
	 * @return
	 */
	@Message("Не указана инсталляция")
	String installationDoesNotSet();
}
