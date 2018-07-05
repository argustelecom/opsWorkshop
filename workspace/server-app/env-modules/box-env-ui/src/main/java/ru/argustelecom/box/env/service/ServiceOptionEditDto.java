package ru.argustelecom.box.env.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;

@Getter
@Setter
@NoArgsConstructor
public class ServiceOptionEditDto {

	private Long id;
	private BusinessObjectDto<TelephonyOptionType> optionType;
	private BusinessObjectDto<AbstractContract<?>> contract;
	private BusinessObjectDto<AbstractTariff> tariff;

	@Builder
	public ServiceOptionEditDto(Long id, BusinessObjectDto<TelephonyOptionType> optionType,
			BusinessObjectDto<AbstractContract<?>> contract, BusinessObjectDto<AbstractTariff> tariff) {
		this.id = id;
		this.optionType = optionType;
		this.contract = contract;
		this.tariff = tariff;
	}

}
