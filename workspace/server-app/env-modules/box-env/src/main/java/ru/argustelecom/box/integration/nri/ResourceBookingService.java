package ru.argustelecom.box.integration.nri;

import ru.argustelecom.box.env.commodity.model.Service;

/**
 * Интеграционный интерфейс сервиса бронирования ресурсов
 * Created by s.kolyada on 18.12.2017.
 */
public interface ResourceBookingService {

	/**
	 * Забронировать ресурсы
	 * @param serviceInstance услуга
	 * @return результат бронирования
	 */
	ResourceBookingResult bookResources(Service serviceInstance);

	/**
	 * Отменить бронь ресурсов под услугу
	 * @param serviceInstance услуга
	 * @return результат выполнения команды
	 */
	ResourceBookingResult releaseBooking(Service serviceInstance);




}
