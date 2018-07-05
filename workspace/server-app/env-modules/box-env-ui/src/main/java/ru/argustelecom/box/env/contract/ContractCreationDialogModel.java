package ru.argustelecom.box.env.contract;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.env.contract.dto.ContractRoleDto;
import ru.argustelecom.box.env.contract.dto.ContractRoleDtoTranslator;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.AbstractContractType;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ContractCreationDialogModel extends AbstractContractCreationDialogModel {

	private static final long serialVersionUID = -426687195329115699L;

	@Inject
	private ContractAppService contractAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private ContractRoleDtoTranslator contractRoleDtoTr;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Getter
	@Setter
	private Date newValidFrom;

	@Getter
	@Setter
	private Date newValidTo;

	@Getter
	@Setter
	private PaymentCondition newPaymentCondition;

	@Getter
	@Setter
	private Location currentLocation;

	private List<? extends AbstractContractType> types;

	@Getter
	@Setter
	private ContractRoleDto newBroker;

	@Getter
	private List<ContractRoleDto> possibleBrokers;

	@Override
	public void onCreationDialogOpen() {
		fillLocationIfExist();
		if (isAgencyContract()) {
			setDefaultBroker();
			refreshPossibleBrokers();
		}

		RequestContext.getCurrentInstance().update("contract_creation_form");
		RequestContext.getCurrentInstance().execute("PF('contractCreationDlgVar').show()");
	}

	@Override
	protected Contract create() {
		return contractAs.createContract(getNewType().getId(), getNewCustomer().getId(), getNewNumber(), newValidFrom,
				newValidTo, newPaymentCondition, newBroker != null ? newBroker.getId() : null);
	}

	@Override
	public String outcome(AbstractContract<?> contract) {
		return outcomeConstructor.construct(ContractCardViewModel.VIEW_ID,
				IdentifiableOutcomeParam.of("contract", contract));
	}

	@Override
	public void cleanCreationParams() {
		super.cleanCreationParams();
		newValidFrom = null;
		newValidTo = null;
	}

	@Override
	public List<? extends AbstractContractType> getTypes() {
		if (types == null)
			types = !isSelectTypeByCustomerType() ? contractTypeRepository.findAllContractTypes()
					: contractTypeRepository.findContractTypes(getNewCustomer().getTypeInstance().getType());
		return types;
	}

	public void onContractTypeChanged() {
		if (isAgencyContract()) {
			setDefaultBroker();
			refreshPossibleBrokers();
		} else {
			setNewBroker(null);
		}
	}

	private void fillLocationIfExist() {
		if (currentLocation != null) {
			Location location = initializeAndUnproxy(currentLocation);
			Location parent = initializeAndUnproxy(currentLocation.getParent());

			if (parent instanceof Building) {
				setNewBuilding(businessObjectDtoTr.translate((Building) parent));
			}
			if (location instanceof Lodging) {
				setNewLodgingType(((Lodging) location).getType());
				setNewLodging(((Lodging) location).getNumber());
			}
		}
	}

	private boolean isAgencyContract() {
		if (getNewType() == null) {
			return false;
		}
		return ((ContractType) getNewType()).getContractCategory().equals(ContractCategory.AGENCY);
	}

	private void setDefaultBroker() {
		newBroker = contractRoleDtoTr.translate(contractAs.findDefaultBroker());
	}

	private void refreshPossibleBrokers() {
		possibleBrokers = contractRoleDtoTr.translate(contractAs.findPossibleBrokers());
	}

}