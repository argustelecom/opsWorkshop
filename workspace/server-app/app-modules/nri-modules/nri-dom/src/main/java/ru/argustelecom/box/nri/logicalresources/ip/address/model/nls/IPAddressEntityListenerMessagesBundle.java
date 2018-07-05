package ru.argustelecom.box.nri.logicalresources.ip.address.model.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface IPAddressEntityListenerMessagesBundle {
	/**
	 * Возвращает сообщение Не указан адрес
	 *
	 * @return
	 */
	@Message("Не указан адрес")
	String addressIsNull();

	/**
	 * Возвращает сообщение Не указано имя для адреса
	 *
	 * @return
	 */
	@Message("Не указано имя для адреса")
	String nameIsNeeded();

	/**
	 * Возвращает сообщение Не указана подсеть
	 *
	 * @return
	 */
	@Message("Не указана подсеть")
	String subnetworkDoesNotSet();

	/**
	 * Возвращает сообщение Не указано имя подсети
	 *
	 * @return
	 */
	@Message("Не указано имя подсети")
	String subnetworkNameDoesNotSet();

	/**
	 * Возвращает сообщение Адреса из диапазона 224.0.0.0 – 239.255.255.255 могут быть только статическими
	 *
	 * @return
	 */
	@Message("Адреса из диапазона 224.0.0.0 – 239.255.255.255 могут быть только статическими")
	String addressFromRangeCanBeStatic();
}
