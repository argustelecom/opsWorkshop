package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.IntStream.range;
import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryExportCsvService.DEFAULT_CSV_SEPARATOR;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.opencsv.CSVReader;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.telephony.tariff.TariffEntryImportResult.TariffEntryImportResultMapper;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.system.inf.exception.BusinessException;

@RunWith(MockitoJUnitRunner.class)
public class TariffExportCsvServiceTest {

	@InjectMocks
	private TariffEntryExportCsvService exportSvc;

	@InjectMocks
	private TariffEntryImportCsvService importSvc;

	private CommonTariff tariff;
	private TariffEntryImportResultMapper mapper;

	@Before
	public void init() {
		tariff = new CommonTariff(-1L);
		tariff.addEntry(createTariffEntry(1L, tariff, "test", newArrayList(10, 20, 30), "Зона 1", new Money("10")));
		tariff.addEntry(createTariffEntry(2L, tariff, "test1", newArrayList(10, 50, 30), "Зона 1", new Money("50")));
		mapper = new TariffEntryImportResultMapper(0, 1, 2, 3);
	}

	@Test
	public void exportSuccess() {
		byte[] export = exportSvc.export(tariff);
		try (CSVReader reader = new CSVReader(new StringReader(new String(export)), DEFAULT_CSV_SEPARATOR)) {
			List<TariffEntry> entries = tariff.getEntries();
			List<TariffEntryImportResult> importEntries = importSvc.parse(reader.readAll(), mapper);
			range(0, importEntries.size()).forEach(index -> {
				assertEquals(entries.get(index).getObjectName(), importEntries.get(index).getName());
				assertEquals(entries.get(index).getZone().getName(), importEntries.get(index).getZoneName());
				assertEquals(entries.get(index).getPrefixes(), importEntries.get(index).getPrefixes());
				assertEquals(entries.get(index).getChargePerUnit().toString(),
						importEntries.get(index).getChargePerUnit().toString());
			});

		} catch (IOException e) {
			throw new BusinessException();
		}
	}

	private TariffEntry createTariffEntry(Long id, AbstractTariff tariff, String name, List<Integer> prefixes,
			String zoneName, Money chargePerUnit) {
		TariffEntry tariffEntry = new TariffEntry(id, tariff);
		TelephonyZone zone = new TelephonyZone(-1L);
		zone.setName(zoneName);
		tariffEntry.update(zone, name, prefixes, chargePerUnit);
		return tariffEntry;
	}
}