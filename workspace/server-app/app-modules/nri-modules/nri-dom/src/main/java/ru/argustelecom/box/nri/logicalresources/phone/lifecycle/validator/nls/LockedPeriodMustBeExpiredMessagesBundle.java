package ru.argustelecom.box.nri.logicalresources.phone.lifecycle.validator.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface LockedPeriodMustBeExpiredMessagesBundle {
	/**
	 * возвращает строку Номер телефона нельзя освободить до истечения срока блокировки
	 *
	 * @return
	 */
	@Message("Номер телефона нельзя освободить до истечения срока блокировки")
	String message();
}
