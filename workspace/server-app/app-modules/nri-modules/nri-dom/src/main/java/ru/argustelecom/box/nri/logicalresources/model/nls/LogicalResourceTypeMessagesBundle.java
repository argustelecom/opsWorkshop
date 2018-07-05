package ru.argustelecom.box.nri.logicalresources.model.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
/**
 * Интерфейс локализации для LogicalResourceType
 */
@MessageBundle(projectCode = "")
public interface LogicalResourceTypeMessagesBundle {
	/**
	 * Возвращает строку  Телефонный номер
	 *
	 * @return
	 */
	@Message("Телефонный номер")
	String phoneNumber();
	/**
	 * Возвращает строку  IP-адрес
	 *
	 * @return
	 */
	@Message("IP-адрес")
	String ipAddress();
	/**
	 * Возвращает строку  Ip-подсеть
	 *
	 * @return
	 */
	@Message("Ip-подсеть")
	String ipSubnet();

}
