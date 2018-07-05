package ru.argustelecom.box.nri.logicalresources.ip.subnet.nls;


import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface IpSubnetCreationDialogModelMessagesBundle {

	/**
	 * Возвращает строку Не удалось создать подсеть
	 *
	 * @return
	 */
	@Message("Не удалось создать подсеть")
	String subnetWasNotCreated();

	/**
	 * Возвращает строку Неверный адрес подсети
	 *
	 * @return
	 */
	@Message("Неверный адрес подсети")
	String wrongSubnetAddress();

	/**
	 * Возвращает строку Адрес подсети указан в неверном формате
	 *
	 * @return
	 */
	@Message("Адрес подсети указан в неверном формате")
	String subnetAddressHasWrongFormat();

	/**
	 * Возвращает строку Возможно имелась в виду подсеть:
	 *
	 * @return
	 */
	@Message("Возможно имелась в виду подсеть: ")
	String maybeYouMentionedThisSubnet();

	/**
	 * Возвращает строку Маска подсети меньше минимального значения
	 *
	 * @return
	 */
	@Message("Маска подсети меньше минимального значения ")
	String subnetMaskLessThanMinimalValue();

	/**
	 * Возвращает строку Длина маски не должна превышать /30
	 *
	 * @return
	 */
	@Message("Длина маски не должна превышать /30")
	String maskCanNotBeLongerThan();

}
