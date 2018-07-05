package ru.argustelecom.box.env.contract;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.contract.dto.ContractRoleDto;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
public class ContractTypeDto extends AbstractContractTypeDto {

	private boolean agency;
	private ContractRoleDto provider;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return ContractTypeDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return ContractType.class;
	}
}
