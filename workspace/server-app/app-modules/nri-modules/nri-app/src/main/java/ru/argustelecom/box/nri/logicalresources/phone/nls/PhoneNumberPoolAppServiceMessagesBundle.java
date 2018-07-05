package ru.argustelecom.box.nri.logicalresources.phone.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации для сервиса работы с пулами
 */
@MessageBundle(projectCode = "")
public interface PhoneNumberPoolAppServiceMessagesBundle {

	/**
	 * Возвращает Пул не может быть удален из-за использования номеров в текущих процессах
	 *
	 * @return
	 */
	@Message("Пул не может быть удален из-за использования номеров в текущих процессах")
	String poolCanNotBeDeletedBecauseNumberIsUsedInBusinessProcess();


	/**
	 * возвращает Начальный номер содержит недопустимые символы
	 *
	 * @return
	 */
	@Message(" Начальный номер содержит недопустимые символы ('X')")
	String starterNumberContainsUnacceptableSymbols();

	/**
	 * Возвращает Конечный номер содержит недопустимые символы
	 *
	 * @return
	 */

	@Message("Конечный номер содержит недопустимые символы ('X')")
	String lastNumberContainsUnacceptableSymbols();

	/**
	 * Возвращает Начальное значение больше конечного
	 *
	 * @return
	 */
	@Message("Начальное значение больше конечного")
	String startValueIsBiggerThanLastValue();

	/**
	 * Возвращает Количество цифр номера не должно превышать 15
	 *
	 * @return
	 */
	@Message("Количество цифр номера не должно превышать 15")
	String quantityDigitsOfPhoneNumberCanNotBeMoreThan();

	/**
	 * Возвращает Начальное и конечное значение имеют неодинаковый формат
	 *
	 * @return
	 */
	@Message("Начальное и конечное значение имеют неодинаковый формат")
	String startValueAndEndValueHaveDifferentFormat();

	/**
	 * Возвращает Невозможно создать номера, т.к. их количество превышает
	 *
	 * @return
	 */
	@Message("Невозможно создать номера, т.к. их количество превышает ")
	String couldNotCreatePhoneNumbersBecauseAmountOfPhoneNumbersMoreThan();

	/**
	 * Возвращает
	 *
	 * @return
	 */
	@Message("всего ")
	String total();

	/**
	 * Возвращает Номера, которые вы собираетесь сгенерировать, уже существуют.
	 *
	 * @return
	 */
	@Message("Номера, которые вы собираетесь сгенерировать, уже существуют.")
	String numbersIsAlreadyExist();


}
