package ru.argustelecom.box.nri.schema.requirements.model;

import lombok.AllArgsConstructor;
import ru.argustelecom.box.inf.nls.LocaleUtils;


/**
 * Все возможные типы требований
 * Created by b.bazarov on 01.02.2018
 */
@AllArgsConstructor
public enum RequirementType {
	/**
	 * Телефонный номер
	 */

	PHONE_NUMBER_BOOKING_REQUIREMENT("{RequirementTypeBundle:ru.argustelecom.box.nri.schema.requirements.phone}"),

	/**
	 * IP-адрес
	 */
	IP_ADDRESS_BOOKING_REQUIREMENT("{RequirementTypeBundle:ru.argustelecom.box.nri.schema.requirements.ip}"),

	/**
	 * Физический ресурс
	 */
	PHYSICAL_RESOURCE_REQUIREMENT("{RequirementTypeBundle:ru.argustelecom.box.nri.schema.requirements.physical}");

	private String caption;

	/**
	 * сообщение
	 * @return
	 */
	public String getCaption(){
		return LocaleUtils.getLocalizedMessage(caption, getClass());
	}
}
