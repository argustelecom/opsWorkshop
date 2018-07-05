package ru.argustelecom.box.env.contract;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.contract.model.AbstractContractType;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ContractExtensionCreationDialogModel extends AbstractContractCreationDialogModel {

	private static final long serialVersionUID = 6896358180059490352L;

	@Inject
	private ContractRepository contractRepository;

	private Contract contract;
	private List<Contract> possibleContracts;

	private Date newDocumentDate;

	@Override
	public void onCreationDialogOpen() {
		RequestContext.getCurrentInstance().update("contract_extension_creation_form");
		RequestContext.getCurrentInstance().execute("PF('contractExtensionCreationDlgVar').show()");
	}

	@Override
	protected ContractExtension create() {
		return contractRepository.createExtension((ContractExtensionType) getNewType(), contract, getNewNumber(),
				newDocumentDate);
	}

	@Override
	public void cleanCreationParams() {
		super.cleanCreationParams();
		newDocumentDate = null;
	}

	@Override
	public List<? extends AbstractContractType> getTypes() {
		return contractTypeRepository.findExtensionTypes(getNewCustomer().getTypeInstance().getType());
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

	public List<Contract> getPossibleContracts() {
		return possibleContracts;
	}

	public void setPossibleContracts(List<Contract> possibleContracts) {
		this.possibleContracts = possibleContracts;
	}

	public Date getNewDocumentDate() {
		return newDocumentDate;
	}

	public void setNewDocumentDate(Date newDocumentDate) {
		this.newDocumentDate = newDocumentDate;
	}

}