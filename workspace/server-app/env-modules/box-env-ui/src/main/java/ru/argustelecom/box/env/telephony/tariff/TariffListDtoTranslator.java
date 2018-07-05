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
public class TariffListDtoTranslator implements DefaultDtoTranslator<TariffListDto, AbstractTariff> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public TariffListDto translate(AbstractTariff tariff) {
		TariffMessagesBundle messages = LocaleUtils.getMessages(TariffMessagesBundle.class);
		return TariffListDto.builder()
				.id(tariff.getId())
				.name(tariff.getObjectName())
				.state(tariff.getState().getName())
				.validFrom(tariff.getValidFrom())
				.validTo(tariff.getValidTo())
				.customer(tariff instanceof CustomTariff
						? businessObjectDtoTr.translate(((CustomTariff) tariff).getCustomer())
						: null)
				.typeName(tariff instanceof CommonTariff ? messages.common() : messages.custom())
				.build();
	}
}