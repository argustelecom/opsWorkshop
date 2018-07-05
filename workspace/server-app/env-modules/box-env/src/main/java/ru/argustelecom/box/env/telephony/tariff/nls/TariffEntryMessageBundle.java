package ru.argustelecom.box.env.telephony.tariff.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface TariffEntryMessageBundle {
	@Message("Зона телефонной нумерации")
	String telephonyZone();

	@Message("Зона")
	String telephonyZoneShort();

	@Message("Направление")
	String name();

	@Message("Стоимость")
	String chargePerUnit();

	@Message("Префиксы")
	String prefixes();

	@Message("Не использовать")
	String columnNotUsed();

	@Message("Повторяющиеся префиксы")
	String repeatedPrefixes();

	@Message("Неподдерживаемый тип файла")
	String unsupportedImportFileType();

	@Message("Ошибка в данных (дубли префиксов, некорректный формат обязательных значений, " +
			"не заполнены обязательные значения)")
	String invalidImportedData();

	@Message("Сведения о классах трафика, которые уже заведены в данном тарифном плане")
	String importedDataAlreadyExist();

	@Message("Данные о классах трафика успешно обработаны и будут импортированы в случае подтверждения")
	String readyToBeImported();

	@Message("Невалидный класс трафика")
	String invalid();

	@Message("Введенные значения")
	String createReportHeader();

	@Message("Классы трафика")
	String tariffEntry();

	@Message("Максимальная длина префикса не должна превышать 9 символов")
	String prefixesLengthInvalid();
}
