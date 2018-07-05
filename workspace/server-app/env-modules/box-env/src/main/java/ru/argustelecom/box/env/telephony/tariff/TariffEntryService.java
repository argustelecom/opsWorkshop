package ru.argustelecom.box.env.telephony.tariff;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.type.CustomType;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CustomTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry;
import ru.argustelecom.box.env.telephony.tariff.model.TariffState;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffEntryMessageBundle;
import ru.argustelecom.box.inf.hibernate.types.IntArrayType;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.validation.ValidationResult;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static javax.persistence.LockModeType.OPTIMISTIC;
import static javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryQueryResult.TARIFF_ENTRY_QUERY_RESULT_MAPPER;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryResultValidator.validateAll;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffEntry.TariffEntryStatus.DEPRECATED;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffState.ACTIVE;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;
import static ru.argustelecom.box.inf.utils.Preconditions.checkCollectionState;
import static ru.argustelecom.system.inf.validation.ValidationResult.success;

@DomainService
public class TariffEntryService implements Serializable {

	private static final String FIND_INTERSECTED_ENTRIES = "TariffEntryService.findByPrefixes";
	private static final String FIND_INTERSECTED_ENTRIES_EXCLUDE = "TariffEntryService.findByPrefixesExclude";
	private static final String FIND_INTERSECTED_PREFIXES = "TariffEntryService.findIntersectedPrexixes";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TariffEntryRepository tariffEntryRp;

	@Inject
	private TariffEntryHistoryRepository tariffEntryHistoryRp;

	@Inject
	private TariffRepository tariffRp;

	@Inject
	private TelephonyZoneRepository telephonyZoneRp;

	@Inject
	private TariffEntryResultValidator validator;

	/**
	 * Добавляет класс трафика только в переданных тарифный план
	 *
	 * @param tariff
	 *            тарифный план
	 * @param zone
	 *            зона телефонной нумерации
	 * @param name
	 *            имя класса трафика
	 * @param prefixes
	 *            список префиксов
	 * @param chargePerUnit
	 *            стоимость класса трафика за тарифицируемую единицу времени
	 * @return класс трафика
	 */
	public TariffEntry create(AbstractTariff tariff, TelephonyZone zone, String name, List<Integer> prefixes,
			Money chargePerUnit, boolean incrementVersion) {
		TariffEntry tariffEntry = tariffEntryRp.create(tariff, zone, name, prefixes, chargePerUnit);
		if (incrementVersion) {
			tariffEntry.setVersion(1L);
		}
		return tariffEntry;
	}

	/**
	 * Добавляет класс трафика в публичный тарифный план и во все индивидуальные тарифы, созданные на основании данного
	 * тарифа
	 *
	 * @param parentTariff
	 *            базовый тарифный план
	 * @param excludeTariffs
	 *            индивидуальные тарифы, в которые не надо добавлять класс трафика. Т.е., в данных тарифах уже
	 *            существует класс трафика
	 * @param zone
	 *            зона телефонной нумерации
	 * @param name
	 *            имя класса трафика
	 * @param prefixes
	 *            список префиксов
	 * @param chargePerUnit
	 *            стоимость класса трафика за тарифицируемую единицу времени
	 * @return класс трафика
	 */
	public TariffEntry createCascade(CommonTariff parentTariff, List<CustomTariff> excludeTariffs, TelephonyZone zone,
			String name, List<Integer> prefixes, Money chargePerUnit) {
		checkNotNull(parentTariff);
		checkNotNull(excludeTariffs);

		List<CustomTariff> tariffs = tariffRp.findBy(parentTariff, ACTIVE);
		excludeTariffs.forEach(tariff -> em.lock(tariff, OPTIMISTIC));
		tariffs.removeAll(excludeTariffs);
		tariffs.forEach(tariff -> tariffEntryRp.create(tariff, zone, name, prefixes, chargePerUnit));
		return create(parentTariff, zone, name, prefixes, chargePerUnit, false);
	}

	/**
	 * Обновляет класс трафика, создавая экземпляр
	 * {@link ru.argustelecom.box.env.telephony.tariff.model.TariffEntryHistory}, если тарифный план находится в
	 * состоянии "Активный"
	 *
	 * @param tariff
	 *            базовый тарифный план
	 * @param entry
	 *            модифицируемый класс трафика
	 * @param zone
	 *            зона телефонной нумерации
	 * @param name
	 *            имя класса трафика
	 * @param prefixes
	 *            список префиксов
	 * @param chargePerUnit
	 *            стоимость класса трафика за тарифицируемую единицу времени
	 */
	public void update(AbstractTariff tariff, TariffEntry entry, TelephonyZone zone, String name,
			List<Integer> prefixes, Money chargePerUnit) {
		if (!entry.equals(zone, name, prefixes, chargePerUnit)) {
			if (ACTIVE.equals(checkNotNull(tariff).getState())) {
				tariffEntryHistoryRp.create(entry);
			}
			em.lock(tariff, OPTIMISTIC_FORCE_INCREMENT);
			entry.update(zone, name, prefixes, chargePerUnit);
			//Иначе версия не успевает подняться
			em.flush();
		}
	}

	/**
	 * Удаляет класс трафика, если тариф находится в состоянии 'Оформление' или 'Анулирован', или устанавливает
	 * состояние {@link TariffEntry.TariffEntryStatus#DEPRECATED} в противном случае
	 *
	 * @param tariff
	 *            тарифный план
	 * @param entry
	 *            удаляемый класс трафика
	 */
	public void remove(AbstractTariff tariff, TariffEntry entry) {
		checkState(checkNotNull(tariff).hasEntry(checkNotNull(entry)));

		if (asList(TariffState.FORMALIZATION, TariffState.CANCELLED).contains(tariff.getState())) {
			tariff.removeEntry(entry);
		} else {
			em.lock(tariff, OPTIMISTIC_FORCE_INCREMENT);
			entry.setStatus(DEPRECATED);
		}
	}

	/**
	 * Валидирует результаты выборки в соответствии с
	 * <a href= "http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6294812">требованиями</a>
	 *
	 * @param entries
	 *            классы трафика, которое необходимо провалидировать
	 * @param zone
	 *            зона телефонной нумерации
	 * @param name
	 *            имя класса трафика
	 * @param chargePerUnit
	 *            стоимость одной тарификацируемой единицы
	 * @return результат валидации
	 */
	public ValidationResult<TariffEntryQueryResult> validate(List<TariffEntryQueryResult> entries, TelephonyZone zone,
			String name, Money chargePerUnit) {
		checkCollectionState(entries, "entries");
		checkNotNull(zone);
		checkState(isNotBlank(checkNotNull(name)));
		checkNotNull(chargePerUnit);

		Function<TariffEntryQueryResult, ValidationResult<TariffEntryQueryResult>> validateEntry = entry -> {
			ValidationResult<TariffEntryQueryResult> result = success();
			TariffEntryMessageBundle messages = getMessages(TariffEntryMessageBundle.class);
			result.add(validator.name(entry, name, messages.telephonyZone()));
			result.add(validator.chargePerUnit(entry, chargePerUnit, messages.chargePerUnit()));
			result.add(validator.zoneName(entry, zone.getName(), messages.telephonyZone()));
			return result;
		};

		return validateAll(entries, validateEntry);
	}

	/**
	 * Ищет все классы трафика c пресекающимися prefix'ами в текущем тарифе и в индивидуальных тарифах, созданных на
	 * основании текущего тарифа
	 *
	 * @param tariff
	 *            тарифный план
	 * @param prefixes
	 *            префиксы
	 * @return все классы трафика из текущего тарифа и всех индивидуальных тарифов, предком которых является текущий
	 *         tariff
	 */
	//@formatter:off
	@NamedNativeQuery(name = FIND_INTERSECTED_ENTRIES, resultSetMapping = TARIFF_ENTRY_QUERY_RESULT_MAPPER,
			query = "WITH parent_children_tariff AS (\n" +
					"    SELECT\n" +
					"      id,\n" +
					"      name\n" +
					"    FROM system.tariff\n" +
					"    WHERE id = :tariffId OR parent_tariff_id = :tariffId\n" +
					")\n" +
					"SELECT\n" +
					"  tariff_entry.id AS id,\n" +
					"  pct.name AS tariff_name,\n" +
					"  tariff_id,\n" +
					"  tariff_entry.name,\n" +
					"  prefix,\n" +
					"  charge_per_unit,\n" +
					"  zone_id,\n" +
					"  tz.name         AS zone_name\n" +
					"FROM parent_children_tariff pct\n" +
					"  JOIN system.tariff_entry ON pct.id = tariff_entry.tariff_id\n" +
					"  JOIN system.telephony_zone tz ON zone_id = tz.id\n" +
					"WHERE prefix && :prefix AND status = 'ACTIVE'")
	//@formatter:on
	@SuppressWarnings("unchecked")
	public List<TariffEntryQueryResult> findByPrefixes(AbstractTariff tariff, List<Integer> prefixes) {
		//@formatter:off
		return em.unwrap(Session.class).getNamedQuery(FIND_INTERSECTED_ENTRIES)
				.setLong("tariffId", tariff.getId())
				.setParameter("prefix", prefixes, new CustomType(new IntArrayType()))
				.list();
		//@formatter:on
	}

	/**
	 * Ищет классы трафика c пресекающимися prefix'ами в тарифе, исключая из выборки текущий класс трафика
	 *
	 * @param tariff
	 *            тарифный план
	 * @param entry
	 *            класс трафика
	 * @param prefixes
	 *            префиксы
	 * @return классы трафика, префиксы которых пересекаются, кроме текущего класса трафика
	 */
	//@formatter:off
	@NamedNativeQuery(name = FIND_INTERSECTED_ENTRIES_EXCLUDE, resultSetMapping = TARIFF_ENTRY_QUERY_RESULT_MAPPER,
			query = "SELECT\n" +
					"  tariff_entry.id AS id,\n" +
					"  t.name          AS tariff_name,\n" +
					"  tariff_id,\n" +
					"  tariff_entry.name,\n" +
					"  prefix,\n" +
					"  charge_per_unit,\n" +
					"  zone_id,\n" +
					"  tz.name         AS zone_name\n" +
					"FROM system.tariff t\n" +
					"  JOIN system.tariff_entry ON t.id = tariff_entry.tariff_id\n" +
					"  JOIN system.telephony_zone tz ON zone_id = tz.id\n" +
					"WHERE t.id = :tariffId AND tariff_entry.id != :tariffEntryId\n" +
					"      AND prefix && :prefix AND status = 'ACTIVE'")
	//@formatter:on
	@SuppressWarnings("unchecked")
	public List<TariffEntryQueryResult> findByPrefixesExclude(AbstractTariff tariff, TariffEntry entry,
			List<Integer> prefixes) {
		checkNotNull(tariff);
		checkNotNull(entry);
		checkCollectionState(prefixes, "prefixes");
		//@formatter:off
		return em.unwrap(Session.class).getNamedQuery(FIND_INTERSECTED_ENTRIES_EXCLUDE)
				.setLong("tariffId", tariff.getId())
				.setParameter("prefix", prefixes, new CustomType(new IntArrayType()))
				.setLong("tariffEntryId", entry.getId())
				.list();
		//@formatter:on
	}

	public List<TariffEntry> importEntries(AbstractTariff tariff, List<TariffEntryImportResult> entries) {
		checkNotNull(tariff);
		checkCollectionState(entries, "entries");
		//@formatter:off
		return entries.stream()
				.map(entry -> create(
						tariff,
						telephonyZoneRp.findBy(entry.getZoneName()),
						entry.getName(),
						entry.getPrefixes(),
						entry.getChargePerUnit(),
						tariff instanceof CustomTariff)
				)
				.collect(toList());
		//@formatter:on
	}

	//@formatter:off
	@NamedNativeQuery(name = FIND_INTERSECTED_PREFIXES, 
			query = "WITH non_unique AS (\n" + 
					"    SELECT ap.*\n" + 
					"    FROM\n" + 
					"      (\n" + 
					"        SELECT all_prefixes.*\n" + 
					"        FROM\n" + 
					"          system.tariff_entry te,\n" + 
					"          LATERAL (\n" + 
					"            SELECT pr AS prefix\n" + 
					"            FROM unnest(te.prefix) pr\n" + 
					"          ) all_prefixes\n" + 
					"        WHERE te.tariff_id IN (:tariffs) \n" + 
					"  		 AND te.status <> 'DEPRECATED' \n" +
					"      ) ap\n" + 
					"    GROUP BY ap.prefix\n" + 
					"    HAVING count(*) > 1\n" + 
					")\n" + 
					"SELECT\n" + 
					"  nu.prefix\n" + 
					"FROM non_unique nu, system.tariff_entry te\n" + 
					"WHERE\n" + 
					"  te.tariff_id IN (:tariffs) \n" + 
					"  AND te.status <> 'DEPRECATED' \n" +
					"  AND cast(ARRAY [nu.prefix] AS INTEGER []) <@ te.prefix\n" + 
					"GROUP BY nu.prefix;")
	//@formatter:on
	public boolean isIntersectedPrefixesExists(Collection<AbstractTariff> tariffs) {
		List<Long> tariffsIds = tariffs.stream().map(AbstractTariff::getId).collect(Collectors.toList());
		return !em.createNamedQuery(FIND_INTERSECTED_PREFIXES).setParameter("tariffs", tariffsIds).getResultList()
				.isEmpty();
	}

	private static final long serialVersionUID = -5320895791053382904L;
}
