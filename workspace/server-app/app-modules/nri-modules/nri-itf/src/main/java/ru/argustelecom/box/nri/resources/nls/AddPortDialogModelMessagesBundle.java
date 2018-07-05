package ru.argustelecom.box.nri.resources.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации для AddPortDialogModel
 */
@MessageBundle(projectCode = "")
public interface AddPortDialogModelMessagesBundle {
	/**
	 * возвращает строку Переданный ресурс null
	 *
	 * @return
	 */
	@Message("Переданный ресурс null")
	String resourceIsNull();

	/**
	 * возвращает строку У ресурса нет спецификации
	 *
	 * @return
	 */
	@Message("У ресурса нет спецификации")
	String resourceDoesNotHaveSpec();

	/**
	 * возвращает строку У ресурса нет поддерживаемых портов
	 *
	 * @return
	 */
	@Message("У ресурса нет поддерживаемых портов")
	String resourceDoesNotSupportPorts();

	/**
	 * возвращает строку Не выбран тип порта
	 *
	 * @return
	 */
	@Message("Не выбран тип порта")
	String portTypeWasNotChosen();

	/**
	 * возвращает строку Тип порта не в списке поддерживаемых
	 *
	 * @return
	 */
	@Message("Тип порта не в списке поддерживаемых")
	String portTypeIsNotSupported();

	/**
	 * возвращает строку Порт null
	 *
	 * @return
	 */
	@Message("Порт null")
	String portIsNull();


}
