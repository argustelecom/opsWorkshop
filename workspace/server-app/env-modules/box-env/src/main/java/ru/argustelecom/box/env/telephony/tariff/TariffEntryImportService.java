package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryImportResult.createInstance;
import static ru.argustelecom.box.inf.utils.Preconditions.checkCollectionState;

import java.nio.charset.Charset;
import java.util.List;

import ru.argustelecom.box.env.telephony.tariff.TariffEntryImportResult.TariffEntryImportResultMapper;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.validation.ValidationResult;

public interface TariffEntryImportService {

	/**
	 * Создает список массивов строк, которые представляют одну сырую строку класса трафика
	 * 
	 * @param content
	 *            содержимое файла
	 * @param charset
	 *            кодировка файла
	 * @return список массивов строк
	 */
	List<String[]> readRawRows(byte[] content, Charset charset);

	/**
	 * Создает сырые классы трафика, используя для извлечения {@link TariffEntryImportResultMapper} массива строк
	 * 
	 * @param rawRows
	 *            список массивов строк, которые представляют одну сырую строку
	 * @param mapper
	 *            индексы элементов массива сырых строк класса трафика
	 * @return список сырых классов трафика
	 */
	default List<TariffEntryImportResult> parse(List<String[]> rawRows, TariffEntryImportResultMapper mapper) {
		checkCollectionState(rawRows, "rawRows");
		checkNotNull(mapper);

		return rawRows.stream().map(rawRow -> createInstance(mapper, rawRow)).collect(toList());
	}

	/**
	 * Валидирует список сырых классов трафика
	 * 
	 * @param tariff
	 *            тарифный план
	 * @param entries
	 *            сырые классы трафика
	 * @return результат валидации сырых классов трафика
	 */
	ValidationResult<TariffEntryImportResult> validate(AbstractTariff tariff, List<TariffEntryImportResult> entries);
}
