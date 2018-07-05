package ru.argustelecom.box.integration.nri;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;

/**
 * Бизнес-интерфейс сервиса для определения тех.возмоэности
 * Created by s.kolyada on 31.08.2017.
 */
public interface TechnicalPossibilityService {

	/**
	 * Проверка технической возможности
	 * @param specification спецификация услуги
	 * @param location расположение объекта для которого проверяется ТВ
	 * @return степень ТВ
	 */
	TechPossibility checkTechnicalPossibility(ServiceSpec specification, Location location);
}
