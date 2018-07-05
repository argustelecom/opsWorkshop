package ru.argustelecom.box.nri.logicalresources.ip.address.nls;


import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации для IPAddressAppService
 */
@MessageBundle(projectCode = "")
public interface IPAddressAppServiceMessagesBundle {
	/**
	 * Возвращает строку У адреса обязательно должно быть имя!
	 *
	 * @return
	 */
	@Message("У адреса обязательно должно быть имя!")
	String ipAddressMustHaveName();

	/**
	 * Возвращает строку У IP-адреса нет ID
	 *
	 * @return
	 */
	@Message("У IP-адреса нет ID")
	String ipAddressDoesNotHaveId();

	/**
	 * Возвращает строку Не указан ID
	 *
	 * @return
	 */
	@Message("Не указан ID")
	String idIsMissed();

	/**
	 * Возвращает строку Не указано новое назначение
	 *
	 * @return
	 */
	@Message("Не указано новое назначение")
	String purposeIsMissed();

}
