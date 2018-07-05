package ru.argustelecom.box.nri.booking.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации для BookingRequirementAppService
 */
@MessageBundle(projectCode = "")
public interface BookingRequirementAppServiceMessagesBundle {

	/**
	 * Возвращает строку Не передано требование
	 *
	 * @return
	 */
	@Message("Не передано требование")
	String requirementIsNeeded();

	/**
	 * Возвращает строку У требования не указана схема
	 *
	 * @return
	 */
	@Message("У требования не указана схема")
	String schemaIsNeededForRequirement();

	/**
	 * Возвращает строку Требование не найдено
	 *
	 * @return
	 */
	@Message("Требование не найдено")
	String requirementCouldNotFind();
}
