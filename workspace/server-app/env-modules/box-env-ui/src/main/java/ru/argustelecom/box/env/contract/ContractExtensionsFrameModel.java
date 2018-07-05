package ru.argustelecom.box.env.contract;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ContractExtensionsFrameModel implements Serializable {

	private static final long serialVersionUID = 4914659955099058906L;

	@Inject
	private ContractRepository contractRepository;

	private Contract contract;
	private List<ContractExtension> extensions;

	public void preRender(Contract contract) {
		if (!Objects.equals(this.contract, contract)) {
			this.contract = contract;
			findExtensions();
		}
	}

	public Callback<Contract> getContractExtensionCallback() {
		return ((contract) -> extensions = contract.getExtensions());
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void findExtensions() {
		extensions = contractRepository.findExtensions(contract);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public List<ContractExtension> getExtensions() {
		return extensions != null ? extensions : Collections.emptyList();
	}

}