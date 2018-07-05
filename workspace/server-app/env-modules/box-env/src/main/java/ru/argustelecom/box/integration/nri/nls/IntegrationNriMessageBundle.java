package ru.argustelecom.box.integration.nri.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Сообщения для интеграции с ТУ в общем окружении
 */
@MessageBundle(projectCode = "")
public interface IntegrationNriMessageBundle {

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Модуль технического учёта недоступен")
	String noNriModulAvailable();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Тип ресурса не может быть пустым")
	String resourceTypeParamShouldNotBeEmpty();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Идентификатор не может быть пустым")
	String idParamShouldNotBeEmpty();

	/**
	 * Возвращает сообщение
	 *
	 * @return
	 */
	@Message("Имя не может быть пустым")
	String nameParamShouldNotBeEmpty();
}
