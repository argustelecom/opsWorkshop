package ru.argustelecom.box.env.telephony.tariff;

import javax.inject.Inject;

import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CustomTariff;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class TariffDtoTranslator implements DefaultDtoTranslator<TariffDto, AbstractTariff> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private TariffEntryDtoTranslator tariffEntryDtoTr;

	@Override
	public TariffDto translate(AbstractTariff tariff) {
		TariffMessagesBundle messages = LocaleUtils.getMessages(TariffMessagesBundle.class);

		return TariffDto.builder()
				.id(tariff.getId())
				.name(tariff.getObjectName())
				.ratedUnit(tariff.getRatedUnit())
				.roundingPolicy(tariff.getRoundingPolicy())
				.state(tariff.getState().getName())
				.type(tariff instanceof CommonTariff ? messages.common() : messages.custom())
				.validFrom(tariff.getValidFrom())
				.validTo(tariff.getValidTo())
				.customer(tariff instanceof CustomTariff ? businessObjectDtoTr.translate(((CustomTariff) tariff).getCustomer()) : null)
				.entries(tariffEntryDtoTr.translate(tariff.getEntries()))
				.parent(tariff instanceof CustomTariff ? businessObjectDtoTr.translate(((CustomTariff) tariff).getParent()) : null)
				.parentEntries(tariff instanceof CustomTariff ? tariffEntryDtoTr.translate(((CustomTariff) tariff).getParent().getEntries()) : null)
				.build();
	}
}
