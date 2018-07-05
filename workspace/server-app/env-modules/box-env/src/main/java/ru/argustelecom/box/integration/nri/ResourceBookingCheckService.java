package ru.argustelecom.box.integration.nri;


import ru.argustelecom.box.env.commodity.model.Service;

/**
 * Интеграционный интерфейс сервиса проверки полноты брони
 * Created by b.bazarov on 09.02.2018.
 */
public interface ResourceBookingCheckService {

	/**
	 * Проверить службу на полноту брони
	 *
	 * @param serviceInstance служба для проверки
	 * @return Результат проверки
	 */
	boolean check(Service serviceInstance);
}
