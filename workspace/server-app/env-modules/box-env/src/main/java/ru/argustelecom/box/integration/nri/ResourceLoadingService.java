package ru.argustelecom.box.integration.nri;

import ru.argustelecom.box.env.commodity.model.Service;

/**
 * Интеграционный интерфейс сервиса нагрузки ресурсов
 * Created by b.bazarov on 21.12.2017.
 */
public interface ResourceLoadingService {

	/**
	 * Нагрузить ресурсы
	 * @param serviceInstance услуга
	 * @return результат нагрузки
	 */
	ResourceLoadingResult loadResources(Service serviceInstance);

	/**
	 * Отменить нагрузку ресурсов услугой
	 * @param serviceInstance услуга
	 * @return результат выполнения команды
	 */
	ResourceLoadingResult releaseLoading(Service serviceInstance);
}
