package ru.argustelecom.box.env.contract;

import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ContractDtoTranslator implements DefaultDtoTranslator<ContractDto, Contract> {

	@Override
	public ContractDto translate(Contract contract) {
		//@formatter:off
		return ContractDto.builder()
				.id(contract.getId())
				.documentNumber(contract.getDocumentNumber())
				.documentType(contract.getType().getObjectName())
				.customer(contract.getCustomer().getObjectName())
				.customerType(contract.getCustomer().getTypeInstance() != null ? contract.getCustomer().getTypeInstance().getType().getObjectName() : null)
				.validFrom(contract.getValidFrom())
				.validTo(contract.getValidTo())
				.state(contract.getState().getName())
				.providerName(contract.getType().getProvider().getObjectName())
				.brokerName(contract.getBroker() != null ? contract.getBroker().getObjectName() : null)
				.build();
		//@formatter:on
	}
}