package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.nio.charset.Charset.forName;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static ru.argustelecom.box.inf.utils.Preconditions.checkCollectionState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import com.opencsv.CSVWriter;

import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.exception.BusinessException;

@ApplicationService
public class TariffEntryExportCsvService implements TariffEntryExportService {

	public static final Charset DEFAULT_CSV_CHARSET = forName("UTF-8");
	public static final char DEFAULT_CSV_SEPARATOR = ';';

	@Override
	public byte[] export(AbstractTariff tariff) {
		//@formatter:off
		List<String[]> rows = checkNotNull(tariff).getEntries().stream()
				.map(entry -> new String[] {
						entry.getObjectName(),
						entry.getPrefixesAsString(),
						entry.getChargePerUnit().toString(),
						entry.getZone().getObjectName()
				})
				.collect(toList());
		//@formatter:on
		return generate(rows, DEFAULT_CSV_SEPARATOR, DEFAULT_CSV_CHARSET);
	}

	@Override
	public byte[] generateValidationReport(List<TariffEntryImportResult> entries) {
		checkCollectionState(entries, "entries");
		//@formatter:off
		List<String[]> rows = entries.stream()
				.map(TariffEntryImportResult::getRawRow)
				.map(rawRow -> stream(rawRow).map(entry -> ofNullable(entry).orElse(EMPTY)).toArray(String[]::new))
				.collect(toList());
		//@formatter:on
		return generate(rows, DEFAULT_CSV_SEPARATOR, DEFAULT_CSV_CHARSET);
	}

	public byte[] generate(List<String[]> rows, char separator, Charset charset) {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(os, charset), separator)) {
				writer.writeAll(rows);
			}
			return os.toByteArray();
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	private static final long serialVersionUID = 8004900516726871166L;
}
