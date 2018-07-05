package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryExportCsvService.DEFAULT_CSV_SEPARATOR;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import com.opencsv.CSVReader;

import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.exception.BusinessException;

@ApplicationService
public class TariffEntryImportCsvService extends DefaultTariffEntryImportService {

	@Override
	public List<String[]> readRawRows(byte[] content, Charset charset) {
		return readRawRows(checkNotNull(content), checkNotNull(charset), DEFAULT_CSV_SEPARATOR);
	}

	public List<String[]> readRawRows(byte[] content, Charset charset, char separator) {
		checkNotNull(content);
		checkNotNull(charset);
		try (CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(content), charset),
				separator)) {
			return reader.readAll();
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}
}
