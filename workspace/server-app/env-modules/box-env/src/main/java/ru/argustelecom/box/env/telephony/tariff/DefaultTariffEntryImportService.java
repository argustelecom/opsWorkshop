package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryResultValidator.validateAll;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;
import static ru.argustelecom.box.inf.utils.Preconditions.checkCollectionState;
import static ru.argustelecom.system.inf.validation.ValidationResult.success;

import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;

import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffEntryMessageBundle;
import ru.argustelecom.system.inf.validation.ValidationResult;

public abstract class DefaultTariffEntryImportService implements TariffEntryImportService {

	@Inject
	protected TariffEntryResultValidator validator;

	@Inject
	protected TelephonyZoneRepository telephonyZoneRp;

	@Override
	public ValidationResult<TariffEntryImportResult> validate(AbstractTariff tariff,
			List<TariffEntryImportResult> entries) {
		checkNotNull(tariff);
		checkCollectionState(entries, "entries");

		Function<TariffEntryImportResult, ValidationResult<TariffEntryImportResult>> validateEntry = importEntry -> {
			TariffEntryMessageBundle messages = getMessages(TariffEntryMessageBundle.class);
			ValidationResult<TariffEntryImportResult> result = success();
			result.add(validator.invalid(importEntry, messages.invalidImportedData()));
			if (importEntry.isValid()) {
				boolean noneZoneExists = telephonyZoneRp.findAll().stream()
						.noneMatch(zone -> zone.getName().equals(importEntry.getZoneName()));
				if (noneZoneExists) {
					result.error(importEntry, messages.invalidImportedData());
				}

				boolean hasDuplicates = entries.size() != entries.stream().distinct().count();
				if (hasDuplicates) {
					result.error(importEntry, messages.invalidImportedData());
				} else {
					//@formatter:off
					entries.stream()
							.filter(entry -> entry.isValid() && !entry.equals(importEntry))
							.forEach(entry -> result.add(validator.prefixes(importEntry, entry.getPrefixes(),
									messages.invalidImportedData())));
					//@formatter:on

				}

				//@formatter:off
				tariff.getEntries()
						.forEach(entry -> result.add(validator.prefixes(importEntry, entry.getPrefixes(),
								messages.importedDataAlreadyExist())));
				//@formatter:on
			}
			if (result.isSuccess()) {
				result.info(importEntry, messages.readyToBeImported());
			}
			return result;
		};

		return validateAll(entries, validateEntry);
	}
}
