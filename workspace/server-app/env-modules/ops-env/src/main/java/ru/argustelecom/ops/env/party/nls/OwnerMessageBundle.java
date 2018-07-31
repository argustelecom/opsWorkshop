package ru.argustelecom.ops.env.party.nls;

import static org.jboss.logging.annotations.Message.Format.MESSAGE_FORMAT;
import static org.jboss.logging.annotations.Message.Format.PRINTF;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface OwnerMessageBundle {

	@Message("Ошибка валидации")
	String qrCodePatternValidationSummary();

	@Message(format = MESSAGE_FORMAT, value = "Выражение \"{0}\" не удовольтворяет шаблону \"{1}\"")
	String invalidQrCodePatternMapping(String mapping, String pattern);

	@Message(format = MESSAGE_FORMAT, value = "Ключевого слова {1} для QR-кода не существует в выражении \"{0}\"")
	String invalidQrCodeItem(String mapping, String actual);

	@Message(format = MESSAGE_FORMAT, value = "Характеристики \"{1}\"  в выражении \"{0}\" не существует")
	String invalidCharacteristic(String mapping, String characteristic);

	@Message(format = MESSAGE_FORMAT, value = "У характеристики \"{1}\" нет \"{2}\" в выражении \"{0}\"")
	String invalidCharacteristicKeyword(String mapping, String characteristic, String keyword);

	@Message("Должны быть перечисленны все обязательные ключевые слова для QR-кода")
	String uniqueItemIsMissing();

	@Message(format = PRINTF, value = "Шаблон вида \"<b>qr_code_keyword={owner_characteristic.keyword},...</b>\","
			+ " где <b>qr_code_keyword</b> может принимать обязательные для заполнения значения: <b>%s</b>"
			+ " и необязательные: <b>%s</b>; <b>owner_characteristic</b> - <b>props</b> для свойств участника и"
			+ " <b>params</b> для параметров владельца; <b>keyword</b> - ключевое слово указаной характеристики")
	String qrCodePatternTooltipHint(String required, String notRequired);
}
