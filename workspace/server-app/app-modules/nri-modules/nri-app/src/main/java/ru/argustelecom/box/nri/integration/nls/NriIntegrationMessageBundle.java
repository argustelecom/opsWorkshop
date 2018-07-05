package ru.argustelecom.box.nri.integration.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface NriIntegrationMessageBundle {
	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Схема подключения услуги не требует бронирования ресурсов")
	String noNeedAnyResourceToBook();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Создано бронирование: ")
	String bookingWasCreated();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Отмена бронирования:")
	String bookingWasCanceled();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Не удалось отменить бронировань:")
	String cancelingOfBookingWasFailed();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Не удалось забронировать все требуемые ресурсы")
	String couldNotBookAllNeededResources();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Ресурсы успешно забронированы")
	String resourcesWereBookedWithSuccess();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Не удалось снять бронирования с некоторых ресурсов")
	String couldNotReleaseSomeResources();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Сняли бронирования с ресурсов")
	String resourcesWereReleased();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Нагружено: ")
	String loaded();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Удалось снять бронирование у нагруженых ресурсов ")
	String allResourcesWereReleased();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Не удалось снять бронирование у нагруженых ресурсов ")
	String someBookingsWereNotReleased();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Ресурсы нагружены")
	String resourcesWereLoaded();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Сняли нагрузку с ресурсов")
	String resourcesWereUnloaded();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Не указана служба для которой требуется провести проверку")
	String serviceDidNotSet();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Найдены требования по одной услуге для разных схем подключения")
	String foundRequirementForDifferentSchemas();

	/**
	 * Возвращает сообщение Схема забронированных ресурсов не соответствует ни одной схеме для услуги
	 *
	 * @return
	 */
	@Message("Схема забронированных ресурсов не соответствует ни одной схеме для услуги ")
	String schemaDoesNotMatchAnyScheme();

	/**
	 * Возвращает сообщение Не указана спецификация услуги для проверки ТВ
	 *
	 * @return
	 */
	@Message("Не указана спецификация услуги для проверки ТВ")
	String specificationDidNotSet();

	/**
	 * Возвращает сообщение Не указан адрес для определения технической возможности
	 *
	 * @return
	 */
	@Message("Не указан адрес для определения технической возможности")
	String locationDidNotSet();


}
