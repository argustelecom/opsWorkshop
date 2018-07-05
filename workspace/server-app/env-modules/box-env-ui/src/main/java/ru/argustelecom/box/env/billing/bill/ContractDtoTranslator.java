package ru.argustelecom.box.env.billing.bill;

import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ContractDtoTranslator implements DefaultDtoTranslator<ContractDto, Contract> {

	@Override
	public ContractDto translate(Contract contract) {
		return ContractDto.builder()
				.id(contract.getId())
				.documentNumber(contract.getDocumentNumber())
				.paymentCondition(contract.getPaymentCondition())
				.build();
	}
}