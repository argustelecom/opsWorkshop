package ru.argustelecom.box.nri.schema.requirements.phone.nls;


import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;


/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface PhoneNumberBookingRequirementRepositoryMessagesBundle {

	/**
	 * возвращает строку Не найдена указанная схема по идентификатору
	 *
	 * @return
	 */
	@Message("Не найдена указанная схема по идентификатору ")
	String couldNotFindSchemaById();

	/**
	 * возвращает строку  для требования
	 *
	 * @return
	 */
	@Message(" для требования ")
	String forRequirement();
}
