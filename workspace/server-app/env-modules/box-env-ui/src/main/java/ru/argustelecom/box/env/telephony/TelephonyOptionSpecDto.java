package ru.argustelecom.box.env.telephony;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TelephonyOptionSpecDto {

	private Long id;
	private Long serviceSpecId;
	private BusinessObjectDto<TelephonyOptionType> optionType;
	private BusinessObjectDto<AbstractTariff> tariff;
}
