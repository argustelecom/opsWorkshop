package ru.argustelecom.box.env.contract;

import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ContractExtensionTypeDtoTranslator
		extends AbstractContractTypeDtoTranslator<ContractExtensionTypeDto, ContractExtensionType> {

	@Override
	protected ContractExtensionTypeDto getDtoToFill() {
		return new ContractExtensionTypeDto();
	}

	@Override
	protected ContractExtensionTypeDto fillDto(ContractExtensionTypeDto dtoToFill, ContractExtensionType contractType) {
		return dtoToFill;
	}

}
