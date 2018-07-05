package ru.argustelecom.box.env.telephony.tariff;

import javax.inject.Inject;

import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntryHistory;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class TariffEntryHistoryDtoTranslator implements DefaultDtoTranslator<TariffEntryHistoryDto, TariffEntryHistory> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public TariffEntryHistoryDto translate(TariffEntryHistory tariffEntryHistory) {
		return TariffEntryHistoryDto.builder()
				.id(tariffEntryHistory.getId())
				.version(tariffEntryHistory.getVersion())
				.employee(businessObjectDtoTr.translate(tariffEntryHistory.getEmployee()))
				.modifiedDate(tariffEntryHistory.getModified())
				.name(tariffEntryHistory.getObjectName())
				.chargePerUnit(tariffEntryHistory.getChargePerUnit())
				.prefixes(tariffEntryHistory.getPrefixes())
				.zone(businessObjectDtoTr.translate(tariffEntryHistory.getZone()))
				.build();
	}
}
