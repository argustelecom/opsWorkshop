package ru.argustelecom.box.env.telephony.tariff;

import static java.util.stream.Collectors.toList;

import javax.inject.Inject;

import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class TariffEntryDtoTranslator implements DefaultDtoTranslator<TariffEntryDto, TariffEntry> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private TariffEntryHistoryDtoTranslator tariffEntryHistoryDtoTr;

	@Override
	public TariffEntryDto translate(TariffEntry tariffEntry) {
		//@formatter:off
		return TariffEntryDto.builder()
				.id(tariffEntry.getId())
				.name(tariffEntry.getObjectName())
				.chargePerUnit(tariffEntry.getChargePerUnit())
				.status(tariffEntry.getStatus())
				.zone(businessObjectDtoTr.translate(tariffEntry.getZone()))
				.prefixes(tariffEntry.getPrefixes())
				.history(tariffEntry.getModificationHistory().stream()
						.map(tariffEntryHistoryDtoTr::translate).collect(toList()))
				.version(tariffEntry.getVersion())
				.build();
	}
	//@formatter:on
}
