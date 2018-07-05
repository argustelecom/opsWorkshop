package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableMap;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.of;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.stl.Money;

/**
 * Содержит данные класса трафика, полученые при импорте. Может быть невалидным, если данные не прошли валидацию
 */
@Getter
@EqualsAndHashCode(of = { "rawRow", "isValid" }, callSuper = true)
public class TariffEntryImportResult extends TariffEntryBaseResult {

	public static final Pattern NAME_PATTERN = compile(".+");
	public static final Pattern PREFIX_PATTERN = compile("^([0-9]{1,9},)*[0-9]{1,9}$");
	public static final Pattern CHARGE_PER_UNIT_PATTERN = compile("[\\d.]+");
	public static final Pattern ZONE_PATTERN = compile(".+");
	private static final Map<Pattern, Function<TariffEntryImportResultMapper, Integer>> VALIDATION_MAPPING;

	static {
		Map<Pattern, Function<TariffEntryImportResultMapper, Integer>> mapping = newHashMap();
		mapping.put(NAME_PATTERN, TariffEntryImportResultMapper::getNameIndex);
		mapping.put(PREFIX_PATTERN, TariffEntryImportResultMapper::getPrefixIndex);
		mapping.put(CHARGE_PER_UNIT_PATTERN, TariffEntryImportResultMapper::getChargePerUnitIndex);
		mapping.put(ZONE_PATTERN, TariffEntryImportResultMapper::getZoneIndex);
		VALIDATION_MAPPING = unmodifiableMap(mapping);
	}

	/**
	 * Сырые данные, на основании которых был создан данный экземпляр
	 */
	private String[] rawRow;
	/**
	 * Указывает, является ли валидным данный экземпляр
	 */
	private boolean isValid;

	protected TariffEntryImportResult(String name, List<Integer> prefixes, Money chargePerUnit, String zoneName,
			String[] rawRow) {
		super(name, prefixes, chargePerUnit, zoneName);
		this.rawRow = rawRow;
		this.isValid = true;
	}

	protected TariffEntryImportResult(String[] rawRow) {
		this.rawRow = rawRow;
	}

	public static TariffEntryImportResult createInstance(TariffEntryImportResultMapper mapper, String[] rawRow) {
		checkNotNull(mapper);
		checkNotNull(rawRow);

		BiPredicate<Pattern, String> validate = (pattern, value) -> pattern.matcher(value).matches();
		Function<String, List<Integer>> extractPrefixes = prefixes -> stream(prefixes.split(DEFAULT_PREFIX_DELIMITER))
				.map(Integer::valueOf).collect(toList());

		boolean isValid = rawRow.length >= mapper.getMaxIndex() && VALIDATION_MAPPING.entrySet().stream()
				.allMatch(entry -> validate.test(entry.getKey(), rawRow[entry.getValue().apply(mapper)]));

		TariffEntryImportResult result;
		if (isValid) {
			//@formatter:off
			result = new TariffEntryImportResult(
					rawRow[mapper.getNameIndex()],
					extractPrefixes.apply(rawRow[mapper.getPrefixIndex()]),
					new Money(rawRow[mapper.getChargePerUnitIndex()]),
					rawRow[mapper.getZoneIndex()],
					rawRow
			);
			//@formatter:on
		} else {
			result = new TariffEntryImportResult(rawRow);
		}
		return result;
	}

	/**
	 * Содержит индексы элементов массива сырых строк класса трафика, с помощью которых можно извлечь конкретные поля
	 * класса трафика
	 */
	@Data
	public static final class TariffEntryImportResultMapper {
		private final int nameIndex;
		private final int prefixIndex;
		private final int chargePerUnitIndex;
		private final int zoneIndex;
		/**
		 * Максимальный индекс, необходим для валидации
		 */
		private final int maxIndex;

		public TariffEntryImportResultMapper(int nameIndex, int prefixIndex, int chargePerUnitIndex, int zoneIndex) {
			int[] indexes = { nameIndex, prefixIndex, chargePerUnitIndex, zoneIndex };
			checkState(of(indexes).peek(index -> checkState(index >= 0)).distinct().count() == indexes.length);
			this.nameIndex = nameIndex;
			this.prefixIndex = prefixIndex;
			this.chargePerUnitIndex = chargePerUnitIndex;
			this.zoneIndex = zoneIndex;
			this.maxIndex = of(indexes).summaryStatistics().getMax();
		}
	}
}
