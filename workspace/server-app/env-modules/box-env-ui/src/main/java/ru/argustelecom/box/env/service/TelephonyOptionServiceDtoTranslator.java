package ru.argustelecom.box.env.service;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class TelephonyOptionServiceDtoTranslator
		implements DefaultDtoTranslator<TelephonyOptionServiceDto, TelephonyOption> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public TelephonyOptionServiceDto translate(TelephonyOption option) {
		AbstractContract<?> contract = option.getSubject().getContract();
		//@formatter:off
		return TelephonyOptionServiceDto.builder()
				.id(option.getId())
				.optionType(businessObjectDtoTr.translate(option.getType()))
				.state(option.getState().getName())
				.subject(businessObjectDtoTr.translate(option.getSubject()))
				.contract(businessObjectDtoTr.translate(contract))
				.contractState(contract.getState())
				.tariff(businessObjectDtoTr.translate(option.getTariff()))
				.createdByProduct(option.getSubject() instanceof ProductOfferingContractEntry)
				.build();
		//@formatter:on
	}

}