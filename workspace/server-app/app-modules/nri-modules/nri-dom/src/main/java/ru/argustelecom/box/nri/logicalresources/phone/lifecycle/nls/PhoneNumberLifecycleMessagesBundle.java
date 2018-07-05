package ru.argustelecom.box.nri.logicalresources.phone.lifecycle.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface PhoneNumberLifecycleMessagesBundle {

	/**
	 * строка Жизненный цикл номера телефона
	 *
	 * @return
	 */
	@Message("Жизненный цикл номера телефона")
	String lifecycleName();

	/**
	 * строка Занять
	 *
	 * @return
	 */
	@Message("Занять")
	String occupy();

	/**
	 * строка Заблокировать
	 *
	 * @return
	 */
	@Message("Заблокировать")
	String lock();

	/**
	 * строка Освободить
	 *
	 * @return
	 */
	@Message("Освободить")
	String unlock();

	/**
	 * строка Вывести из обращения
	 *
	 * @return
	 */
	@Message("Вывести из обращения")
	String delete();
}
