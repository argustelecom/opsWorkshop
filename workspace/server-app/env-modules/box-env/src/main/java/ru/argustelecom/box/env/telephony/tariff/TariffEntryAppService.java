package ru.argustelecom.box.env.telephony.tariff;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.telephony.tariff.TariffEntryImportResult.TariffEntryImportResultMapper;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CustomTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.reportengine.OutputType;
import ru.argustelecom.system.inf.validation.ValidationResult;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.inf.utils.Preconditions.checkCollectionState;

@ApplicationService
public class TariffEntryAppService implements Serializable {

	private static final long serialVersionUID = 8725723753825900047L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TariffEntryService tariffEntrySvc;

	@Inject
	private TariffEntryImportCsvService importCsvSvc;

	@Inject
	private TariffEntryExportCsvService exportCsvSvc;

	public TariffEntry create(Long tariffId, Long zoneId, String name, List<Integer> prefix, Money chargePerUnit,
			List<Long> customTariffIds) {
		checkNotNull(tariffId);
		checkNotNull(zoneId);
		checkNotNull(name);
		checkNotNull(prefix);
		checkNotNull(chargePerUnit);
		checkArgument(!prefix.isEmpty());

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		TelephonyZone zone = em.find(TelephonyZone.class, zoneId);
		checkNotNull(zone);

		if (tariff instanceof CommonTariff) {
			checkArgument(customTariffIds != null);
			List<CustomTariff> customTariffs = customTariffIds.stream()
					.map(customTariffId -> em.find(CustomTariff.class, customTariffId)).collect(toList());

			return tariffEntrySvc.createCascade((CommonTariff) tariff, customTariffs, zone, name, prefix,
					chargePerUnit);
		} else {
			return tariffEntrySvc.create(tariff, zone, name, prefix, chargePerUnit, true);
		}
	}

	public void update(Long tariffId, Long tariffEntryId, Long telephonyZoneId, String name, List<Integer> prefixes,
			Money chargePerUnit) {
		checkArgument(name != null && !name.isEmpty());
		checkArgument(prefixes != null && !prefixes.isEmpty());
		checkNotNull(chargePerUnit);

		TariffEntry entry = em.find(TariffEntry.class, tariffEntryId);
		checkNotNull(entry);

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		TelephonyZone zone = em.find(TelephonyZone.class, telephonyZoneId);
		checkNotNull(zone);

		tariffEntrySvc.update(tariff, entry, zone, name, prefixes, chargePerUnit);
	}

	public void remove(Long tariffId, Long tariffEntryId) {
		checkNotNull(tariffId);
		checkNotNull(tariffEntryId);

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		TariffEntry entry = em.find(TariffEntry.class, tariffEntryId);
		checkNotNull(entry);

		tariffEntrySvc.remove(tariff, entry);
	}

	public List<TariffEntryQueryResult> findByPrefixes(Long tariffId, List<Integer> prefixes) {
		checkNotNull(tariffId);

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		return tariffEntrySvc.findByPrefixes(tariff, prefixes);
	}

	public List<TariffEntryQueryResult> findByPrefixesExclude(Long tariffId, Long entryId, List<Integer> prefixes) {
		checkNotNull(tariffId);
		checkNotNull(entryId);

		AbstractTariff tariff = em.find(AbstractTariff.class, tariffId);
		checkNotNull(tariff);

		TariffEntry entry = em.find(TariffEntry.class, entryId);
		checkNotNull(entry);

		return tariffEntrySvc.findByPrefixesExclude(tariff, entry, prefixes);
	}

	public ValidationResult<TariffEntryQueryResult> validate(List<TariffEntryQueryResult> entries, Long zoneId,
			String name, Money chargePerUnit) {
		checkArgument(name != null && !name.isEmpty());
		checkNotNull(zoneId);

		TelephonyZone zone = em.find(TelephonyZone.class, zoneId);
		checkNotNull(zone);

		return tariffEntrySvc.validate(entries, zone, name, chargePerUnit);
	}

	public List<String[]> readRawRows(OutputType type, byte[] content, Charset charset) {
		return getImportService(checkNotNull(type)).readRawRows(checkNotNull(content), checkNotNull(charset));
	}

	public boolean isIntersectedPrefixesExists(Collection<Long> tariffsIds) {
		return tariffEntrySvc.isIntersectedPrefixesExists(tariffsIds.stream()
				.map(tariffId -> em.getReference(AbstractTariff.class, tariffId)).collect(Collectors.toList()));
	}

	public List<TariffEntryImportResult> parse(OutputType type, TariffEntryImportResultMapper mapper,
			List<String[]> rawRows) {
		return getImportService(checkNotNull(type)).parse(checkNotNull(rawRows), checkNotNull(mapper));
	}

	public ValidationResult<TariffEntryImportResult> validate(Long tariffId, OutputType type,
			List<TariffEntryImportResult> entries) {
		checkNotNull(tariffId);
		checkNotNull(type);
		checkCollectionState(entries, "entries");
		AbstractTariff tariff = checkNotNull(em.getReference(AbstractTariff.class, tariffId));
		return checkNotNull(getImportService(type)).validate(tariff, entries);
	}

	public List<TariffEntry> importEntries(Long tariffId, OutputType type, List<TariffEntryImportResult> entries) {
		checkNotNull(tariffId);
		checkNotNull(type);
		checkCollectionState(entries, "entries");
		AbstractTariff tariff = checkNotNull(em.getReference(AbstractTariff.class, tariffId));
		return tariffEntrySvc.importEntries(tariff, entries);
	}

	public byte[] generateValidationReport(OutputType type, List<TariffEntryImportResult> entries) {
		checkNotNull(type);
		checkCollectionState(entries, "entries");
		return getExportService(type).generateValidationReport(entries);
	}

	public byte[] export(OutputType type, Long tariffId) {
		return getExportService(checkNotNull(type))
				.export(checkNotNull(em.find(AbstractTariff.class, checkNotNull(tariffId))));
	}

	private TariffEntryImportService getImportService(OutputType type) {
		//@formatter:off
		switch (type) {
		case CSV: return importCsvSvc;
		default: throw new SystemException("Unsupported type");
		}
		//@formatter:on
	}

	private TariffEntryExportService getExportService(OutputType type) {
		//@formatter:off
		switch (type) {
		case CSV: return exportCsvSvc;
		default: throw new SystemException("Unsupported type");
		}
		//@formatter:on
	}
}
