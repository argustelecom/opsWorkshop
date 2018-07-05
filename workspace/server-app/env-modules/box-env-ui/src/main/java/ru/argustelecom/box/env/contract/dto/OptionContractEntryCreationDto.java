package ru.argustelecom.box.env.contract.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;

/**
 * Dto для диалога создания позиции договора на основании
 * {@linkplain ru.argustelecom.box.env.commodity.telephony.model.Option опции}.
 */
@Getter
@Setter
@NoArgsConstructor
public class OptionContractEntryCreationDto {

	private OptionEntryServiceDto service;
	private BusinessObjectDto<TelephonyOptionType> optionType;
	private BusinessObjectDto<AbstractTariff> tariff;

}