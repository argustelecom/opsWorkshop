package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.telephony.tariff.HasPrefixes.DEFAULT_PREFIX_DELIMITER;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryExportCsvService.DEFAULT_CSV_CHARSET;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.telephony.tariff.TariffEntryImportResult.TariffEntryImportResultMapper;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffEntryMessageBundle;
import ru.argustelecom.system.inf.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class TariffImportCsvServiceTest {
	//@formatter:off
	public static final String VALID_FILE_CONTENT =
			"Россия, мобильные МегаФон;;1020,1238,120;1.28;Междугородняя mob\n" +
			"Россия, МТС;;1020,123;1.32;Междугородняя mob";
	public static final String PARTIAL_FILE_CONTENT =
			"Россия, мобильные МегаФон;;1020dsaf,1238,120;1.28;Междугородняя mob\n" +
			"Россия, МТС;;1020,40;1.28;Междугородняя mob";
	public static final String INVALID_FILE_CONTENT =
			"Россия, мобильные МегаФон;;1020asf,1238,120;1.28;Междугородняя mob\n" +
			"Россия, МТС;;1020;;Междугородняя mob";
	//@formatter:on

	@InjectMocks
	private TariffEntryImportCsvService importSvc;

	@InjectMocks
	private TariffEntryResultValidator validator;

	private List<String[]> validRawRows;
	private List<String[]> partiallyValidRawRows;
	private List<String[]> invalidRawRows;
	private CommonTariff commonTariff;
	private TariffEntry entry;
	private TelephonyZone zone;
	private TariffEntryImportResultMapper mapper;

	@Before
	public void initRawRows() {
		validRawRows = importSvc.readRawRows(VALID_FILE_CONTENT.getBytes(DEFAULT_CSV_CHARSET), DEFAULT_CSV_CHARSET);
		partiallyValidRawRows = importSvc.readRawRows(PARTIAL_FILE_CONTENT.getBytes(DEFAULT_CSV_CHARSET),
				DEFAULT_CSV_CHARSET);
		invalidRawRows = importSvc.readRawRows(INVALID_FILE_CONTENT.getBytes(DEFAULT_CSV_CHARSET), DEFAULT_CSV_CHARSET);
		commonTariff = new CommonTariff(10L);
		entry = new TariffEntry(10L, commonTariff);
		zone = new TelephonyZone(10L);
		entry.update(zone, "test", newArrayList(10, 20, 30, 40), new Money("10"));
		commonTariff.addEntry(entry);
		mapper = new TariffEntryImportResultMapper(0, 2, 3, 4);
	}

	@Test
	public void testReadSuccess() {
		List<String[]> rawRows = importSvc.readRawRows(VALID_FILE_CONTENT.getBytes(DEFAULT_CSV_CHARSET),
				DEFAULT_CSV_CHARSET);
		assertEquals(2, rawRows.size());
		rawRows.forEach(rawRow -> assertEquals(5, rawRow.length));
	}

	@Test
	public void testParseFail() {
		List<TariffEntryImportResult> result = importSvc.parse(invalidRawRows, mapper);
		result.forEach(entry -> {
			assertEquals(false, entry.isValid());
			assertEquals(true, entry.getRawRow() != null);
		});
	}

	@Test
	public void testParseSuccess() {
		List<TariffEntryImportResult> result = importSvc.parse(validRawRows, mapper);
		range(0, validRawRows.size()).forEach(index -> {
			TariffEntryImportResult entry = result.get(index);
			String[] rawRow = validRawRows.get(index);
			assertEquals(rawRow[mapper.getNameIndex()], entry.getName());
			List<Integer> expected = newArrayList(rawRow[mapper.getPrefixIndex()].split(DEFAULT_PREFIX_DELIMITER))
					.stream().map(Integer::valueOf).collect(toList());
			assertEquals(expected, entry.getPrefixes());
			assertEquals(new Money(rawRow[mapper.getChargePerUnitIndex()]), entry.getChargePerUnit());
			assertEquals(rawRow[mapper.getZoneIndex()], entry.getZoneName());
			assertEquals(true, entry.isValid());
			assertEquals(true, entry.getRawRow() != null);
		});
		result.forEach(entry -> assertEquals(true, entry.isValid()));
	}

	@Ignore
	@Test
	public void testValidationSuccess() {
		List<TariffEntryImportResult> importResult = importSvc.parse(validRawRows, mapper);
		ValidationResult<TariffEntryImportResult> validationResult = importSvc.validate(commonTariff, importResult);
		assertEquals(true, validationResult.isSuccess());
	}

	@Ignore
	@Test
	public void testValidationFail() {
		List<TariffEntryImportResult> importResult = importSvc.parse(partiallyValidRawRows, mapper);
		ValidationResult<TariffEntryImportResult> validationResult = importSvc.validate(commonTariff, importResult);
		TariffEntryMessageBundle messages = getMessages(TariffEntryMessageBundle.class);
		assertEquals(false, validationResult.isSuccess());
		validationResult.getErrors().forEach(resultEntry -> {
			if (resultEntry.getSource().isValid()) {
				assertEquals(messages.prefixes(), resultEntry.getMessage());
			} else {
				assertEquals(messages.invalidImportedData(), resultEntry.getMessage());
			}
		});

	}

}