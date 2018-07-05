package ru.argustelecom.box.nri.booking.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации для сервиса бронирования
 */
@MessageBundle(projectCode = "")
public interface BookingAppServiceMessagesBundle {

	/**
	 * не поддерживаемый тип
	 * @return
	 */
	@Message("Не поддерживаемый тип требований")
	String unsupportedRequirementType();

	/**
	 * "Не передан экземпляр услуги"
	 * @return
	 */
	@Message("Не передан экземпляр услуги")
	String didNotPassServiceInstance();

	/**
	 * "Не передано требование по которому совершается бронирование"
	 * @return
	 */
	@Message("Не передано требование по которому совершается бронирование")
	String didNotPassRequirementForBooking();

	/**
	 * "Не передан список телефонов для бронирования"
	 * @return
	 */
	@Message("Не передан список телефонов для бронирования")
	String didNotPassPhoneNumberListForBooking();

}

