package ru.argustelecom.box.nri.logicalresources.phone.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface PhoneNumberAppServiceMessagesBundle {
	/**
	 * Возвращает строку У переданного элемента отсутствует ID
	 *
	 * @return
	 */
	@Message("У переданного элемента отсутствует ID")
	String idIsNull();

	/**
	 * Возвращает строку Номер с ID =
	 *
	 * @return
	 */
	@Message("Номер с ID = ")
	String numberWithId();

	/**
	 * Возвращает строку  не найден
	 *
	 * @return
	 */
	@Message(" не найден")
	String isMissed();

	/**
	 * Возвращает строку Удаление номеров в статусе "Занят" невозможно.
	 *
	 * @return
	 */
	@Message("Удаление номеров в статусе \"Занят\" невозможно.")
	String occupy();

	/**
	 * Возвращает строку Удаление забронированных номеров невозможно.
	 *
	 * @return
	 */
	@Message("Удаление забронированных номеров невозможно.")
	String booked();

	/**
	 * Возвращает строку Не указано требование для поиска
	 *
	 * @return
	 */
	@Message("Не указано требование для поиска")
	String search();

	/**
	 * Возвращает строку Номера не были удалены
 	 * @param numbers номера до 5
	 * @param moreNumbers сколько ещё
	 * @return
	 */
	@Message("Номера не были удалены: %s%s")
	String numbersWasNotDeleted(String numbers, String moreNumbers);

	/**
	 * Возвращает строку и еще
	 *
	 * @return
	 */
	@Message(" и еще ")
	String andMore();

	/**
	 * Возвращает строку Номер не был удален
	 * @param number номер
	 * @return
	 */
	@Message("Номер не был удален: %s")
	String phoneNumberWasNotDeleted(String number);


}
