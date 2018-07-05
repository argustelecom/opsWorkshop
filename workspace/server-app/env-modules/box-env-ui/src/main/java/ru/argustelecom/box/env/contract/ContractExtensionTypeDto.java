package ru.argustelecom.box.env.contract;

import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@NoArgsConstructor
public class ContractExtensionTypeDto extends AbstractContractTypeDto {
	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return ContractExtensionTypeDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return ContractExtensionType.class;
	}
}
