package ru.argustelecom.box.integration.nri.service;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.integration.nri.service.model.ResourceRepresentation;

import java.util.Set;

/**
 * Интерфес для сервиса получения информации по сулге из ТУ
 * см. BOX-2738
 * Created by s.kolyada on 11.04.2018.
 */
public interface ServiceInfoService {

	/**
	 * Получить все нагруженные услугой ресурсы
	 * @param service услуга
	 * @return список описания ресурсов, который нагружены услугой
	 */
	Set<ResourceRepresentation> allLoadedResourcesByService(Service service);
}
