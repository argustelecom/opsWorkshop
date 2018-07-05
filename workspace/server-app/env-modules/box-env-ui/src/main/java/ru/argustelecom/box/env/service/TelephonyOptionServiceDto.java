package ru.argustelecom.box.env.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class TelephonyOptionServiceDto extends ConvertibleDto {

	private Long id;
	private BusinessObjectDto<TelephonyOptionType> optionType;
	private String state;
	private BusinessObjectDto<ContractEntry> subject;
	private BusinessObjectDto<AbstractContract<?>> contract;
	private ContractState contractState;
	private BusinessObjectDto<AbstractTariff> tariff;
	private boolean createdByProduct;

	@Builder
	public TelephonyOptionServiceDto(Long id, BusinessObjectDto<TelephonyOptionType> optionType, String state,
			BusinessObjectDto<ContractEntry> subject, BusinessObjectDto<AbstractContract<?>> contract,
			ContractState contractState, BusinessObjectDto<AbstractTariff> tariff, boolean createdByProduct) {
		this.id = id;
		this.optionType = optionType;
		this.state = state;
		this.subject = subject;
		this.contract = contract;
		this.contractState = contractState;
		this.tariff = tariff;
		this.createdByProduct = createdByProduct;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return TelephonyOptionServiceDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return TelephonyOption.class;
	}

}