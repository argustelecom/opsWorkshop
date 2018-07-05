package ru.argustelecom.box.nri.logicalresources.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface PhoneNumberMaskValidatorMessagesBundle {


	/**
	 * строка Ошибка, маски
	 *
	 * @return
	 */
	@Message("Ошибка, маски ")
	String maskError();

	/**
	 * строка маска должна содержать +, любые цифры, (, ), дефис, пробел
	 *
	 * @return
	 */
	@Message("маска должна содержать +, любые цифры, (, ), дефис, пробел")
	String maskNeedsToContain();

	/**
	 * строка Телефонный номер не может быть дленнее 15 символов
	 *
	 * @return
	 */
	@Message("Телефонный номер не может быть дленнее 15 символов")
	String phoneNumberLenghtNeedToBe();
}
