package ru.argustelecom.box.env.contract;

import javax.inject.Inject;

import ru.argustelecom.box.env.contract.dto.ContractRoleDtoTranslator;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ContractTypeDtoTranslator extends AbstractContractTypeDtoTranslator<ContractTypeDto, ContractType> {

	@Inject
	private ContractRoleDtoTranslator contractRoleDtoTr;

	@Override
	protected ContractTypeDto getDtoToFill() {
		return new ContractTypeDto();
	}

	@Override
	protected ContractTypeDto fillDto(ContractTypeDto dtoToFill, ContractType contractType) {
		dtoToFill.setAgency(contractType.getContractCategory().equals(ContractCategory.AGENCY));
		dtoToFill.setProvider(contractRoleDtoTr.translate(contractType.getProvider()));
		return dtoToFill;
	}
}
