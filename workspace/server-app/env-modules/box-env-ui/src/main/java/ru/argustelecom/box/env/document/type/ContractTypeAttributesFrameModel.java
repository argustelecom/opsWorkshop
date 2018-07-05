package ru.argustelecom.box.env.document.type;

import javax.inject.Inject;

import ru.argustelecom.box.env.contract.AbstractContractTypeDto;
import ru.argustelecom.box.env.contract.ContractTypeAppService;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ContractTypeAttributesFrameModel extends DocumentTypeAttributesFrameModel<AbstractContractTypeDto> {

	@Inject
	private ContractTypeAppService contractTypeAppService;

	public void save() {
		AbstractContractTypeDto documentTypeDto = getDocumentTypeDto();
		if (isContractType()) {
			contractTypeAppService.saveContractType(documentTypeDto.getId(),
					documentTypeDto.getCustomerTypeDto().getId(), documentTypeDto.getName(),
					documentTypeDto.getDescription());
		} else if (isContractExtensionType()) {
			contractTypeAppService.saveContractExtensionType(documentTypeDto.getId(),
					documentTypeDto.getCustomerTypeDto().getId(), documentTypeDto.getName(),
					documentTypeDto.getDescription());
		}
	}

	public boolean isContractType() {
		if (getDocumentTypeDto() == null) {
			return false;
		}
		return getDocumentTypeDto().getEntityClass().equals(ContractType.class);
	}

	public boolean isContractExtensionType() {
		if (getDocumentTypeDto() == null) {
			return false;
		}
		return getDocumentTypeDto().getEntityClass().equals(ContractExtensionType.class);
	}

	private static final long serialVersionUID = -3151175455632674952L;

}