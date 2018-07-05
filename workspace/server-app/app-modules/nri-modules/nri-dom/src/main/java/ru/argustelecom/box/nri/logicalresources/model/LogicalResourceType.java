package ru.argustelecom.box.nri.logicalresources.model;

import lombok.AllArgsConstructor;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.logicalresources.model.nls.LogicalResourceTypeMessagesBundle;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Все возможные типы логических ресурсов
 * Created by s.kolyada on 27.10.2017.
 */
@AllArgsConstructor
public enum LogicalResourceType {

	/**
	 * Телефонный номер
	 */
	PHONE_NUMBER,

	/**
	 * IP-адрес
	 */
	IP_ADDRESS,

	/**
	 * Ip-подсеть
	 */
	IP_SUBNET;

	public String getName(){
		LogicalResourceTypeMessagesBundle messages = LocaleUtils.getMessages(LogicalResourceTypeMessagesBundle.class);
		switch (this){
			case PHONE_NUMBER:
				return messages.phoneNumber();
			case IP_SUBNET:
				return messages.ipSubnet();
			case IP_ADDRESS:
				return messages.ipAddress();
			default:
				throw new SystemException("Unsupported LogicalResourceType");
		}
	}
}