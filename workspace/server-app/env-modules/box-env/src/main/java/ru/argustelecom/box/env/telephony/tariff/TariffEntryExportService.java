package ru.argustelecom.box.env.telephony.tariff;

import java.io.Serializable;
import java.util.List;

import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;

public interface TariffEntryExportService extends Serializable {
	/**
	 * Экспортирует информацию о тарифном плане (классы трафика)
	 * 
	 * @param tariff
	 *            экспортируемый тарифный план
	 * @return содержимое сгенерированого файла
	 */
	byte[] export(AbstractTariff tariff);

	/**
	 * Генерирует отчет о невалидных классах трафика, полученных при импорте
	 *
	 * @param entries
	 *            импортируемые классы трафика
	 * @return отчет, содержащий невалидные классы трафика
	 */
	byte[] generateValidationReport(List<TariffEntryImportResult> entries);
}
