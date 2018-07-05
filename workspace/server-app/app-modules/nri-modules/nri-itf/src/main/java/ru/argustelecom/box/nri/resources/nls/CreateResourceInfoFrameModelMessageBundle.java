package ru.argustelecom.box.nri.resources.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации для CreateResourceInfoFrameModel
 */
@MessageBundle(projectCode = "")
public interface CreateResourceInfoFrameModelMessageBundle {

	/**
	 * Возвращает строку Не все обязательные параметры заполнены
	 * @return
	 */
	@Message("Не все обязательные параметры заполнены")
	String someRequiredParametersDidNotSet();

	/**
	 * Возвращает строку Ошибка преобразования
	 * @return
	 */
	@Message("Ошибка преобразования")//"Conversion Error"
	String conversionError();

	/**
	 * Возвращает строку Не верная спецификация
	 * @return
	 */
	@Message("Не верная спецификация")//Not a valid specification.
	String notValidSpecification();

}
