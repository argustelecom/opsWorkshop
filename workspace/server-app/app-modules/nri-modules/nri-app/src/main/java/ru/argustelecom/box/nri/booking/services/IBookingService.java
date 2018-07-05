package ru.argustelecom.box.nri.booking.services;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;

/**
 * Интерфейс для сервисов бронирования конкретных ресурсов
 * Created by s.kolyada on 19.12.2017.
 */
public interface IBookingService {

	/**
	 * Забронировать ресурс
	 *
	 * @param serviceInstance услуга
	 * @param requirement     требования к бронируемому ресурсу
	 * @return наряд на бронирование
	 */
	BookingOrder book(Service serviceInstance, ResourceRequirement requirement);
}
