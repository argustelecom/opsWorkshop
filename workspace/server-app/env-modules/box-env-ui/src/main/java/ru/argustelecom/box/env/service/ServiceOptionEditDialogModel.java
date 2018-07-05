package ru.argustelecom.box.env.service;

import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.OptionType;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionAppService;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.contract.ContractAppService;
import ru.argustelecom.box.env.contract.ContractEntryAppService;
import ru.argustelecom.box.env.contract.ContractTypeAppService;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.contract.model.OptionContractEntry;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.telephony.tariff.TariffAppService;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CustomTariff;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("serviceOptionEditDm")
@PresentationModel
public class ServiceOptionEditDialogModel implements Serializable {

	private static final long serialVersionUID = -1884261532304942246L;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private ContractAppService contractAs;

	@Inject
	private ContractEntryAppService contractEntryAs;

	@Inject
	private TariffAppService tariffAs;

	@Inject
	private TelephonyOptionAppService telephonyOptionAs;

	@Inject
	private TelephonyOptionServiceDtoTranslator telephonyOptionServiceDtoTr;

	@Inject
	private ContractTypeAppService contractTypeAs;

	@Inject
	private ServiceContextOptionDtoTranslator serviceContextOptionDtoTr;

	@Setter
	private Callback<TelephonyOptionServiceDto> callback;

	@Getter
	private List<BusinessObjectDto<PartyRole>> possibleBrokers;

	@Getter
	@Setter
	private boolean needCreateContract = true;

	@Getter
	@Setter
	private ServiceOptionEditDto newOption = new ServiceOptionEditDto();

	@Getter
	@Setter
	private ServiceContractCreationDto newContract;

	private ServiceContextOptionDto service;

	@Getter
	private List<? extends BusinessObjectDto<? extends OptionType>> optionTypes;

	@Getter
	private List<BusinessObjectDto<Contract>> contracts;

	@Getter
	private List<BusinessObjectDto<AbstractTariff>> tariffs;

	public void onDialogOpen(String formId) {
		RequestContext.getCurrentInstance().reset(formId);

		initOptionTypes();
		initContracts();
		initTariffs();

		RequestContext.getCurrentInstance().update(formId);
		RequestContext.getCurrentInstance().execute("PF('serviceOptionEditDlgVar').show()");
	}

	public void submit(String update) {
		if (!isEditMode()) {
			if (needCreateContract) {
				newContract = new ServiceContractCreationDto();
				if (isAgencyContract()) {
					setDefaultBroker();
					refreshPossibleBrokers();
				}
				RequestContext.getCurrentInstance().execute("PF('serviceOptionWizard').next()");
				return;
			}

			callback.execute(createOption(newOption.getContract().getId()));
		} else {
			callback.execute(telephonyOptionServiceDtoTr
					.translate(telephonyOptionAs.changeTariff(newOption.getId(), newOption.getTariff().getId())));
		}

		RequestContext.getCurrentInstance().execute("PF('serviceOptionEditDlgVar').hide()");
		RequestContext.getCurrentInstance().update(Arrays.asList(update.split(" ")));
		cancel();
	}

	public void submitWithContract() {
		Contract contract = contractAs.createContract(newContract.getContractType().getId(), service.getCustomerId(),
				newContract.getNumber(), newContract.getValidFrom(), newContract.getValidTo(),
				newContract.getPaymentCondition(),
				newContract.getBroker() != null ? newContract.getBroker().getId() : null);

		callback.execute(createOption(contract.getId()));
		cancel();
	}

	public void cancel() {
		newOption = new ServiceOptionEditDto();
		callback = null;
		needCreateContract = true;
	}

	public void setEditableOption(TelephonyOptionServiceDto editableOptionDto) {
		newOption = ServiceOptionEditDto.builder().id(editableOptionDto.getId())
				.optionType(editableOptionDto.getOptionType()).contract(editableOptionDto.getContract())
				.tariff(editableOptionDto.getTariff()).build();
	}

	public void setService(Service service) {
		this.service = serviceContextOptionDtoTr.translate(service);
	}

	private void initOptionTypes() {
		optionTypes = service.getOptionTypes();
	}

	private void initTariffs() {
		List<AbstractTariff> activeTariffs = tariffAs.findActiveTariffs();
		activeTariffs.removeIf(otherCustomersTariff());
		tariffs = businessObjectDtoTr.translate(activeTariffs);
	}

	private void initContracts() {
		contracts = businessObjectDtoTr.translate(contractAs.findContractsInFormalization(service.getCustomerId()));
	}

	private Predicate<AbstractTariff> otherCustomersTariff() {
		return tariff -> {
			Customer customer = ((Service) service.getIdentifiable()).getSubject().getContract().getCustomer();
			return tariff instanceof CustomTariff && !((CustomTariff) tariff).getCustomer().equals(customer);
		};
	}

	public List<BusinessObjectDto<ContractType>> getTypes() {
		return businessObjectDtoTr.translate(contractTypeAs.findContractTypes(service.getCustomerTypeId()));
	}

	public void onContractTypeChanged() {
		if (isAgencyContract()) {
			setDefaultBroker();
			refreshPossibleBrokers();
		}
	}

	public boolean isEditMode() {
		return newOption.getId() != null;
	}

	private TelephonyOptionServiceDto createOption(Long contractId) {
		OptionContractEntry contractEntry = contractEntryAs.createOptionEntry(contractId, service.getId(),
				newOption.getOptionType().getId(), newOption.getTariff().getId());
		TelephonyOption option = (TelephonyOption) contractEntry.getOptions().stream().findFirst()
				.orElseThrow(() -> new SystemException("Option not found"));
		checkState(option.getType().getId().equals(newOption.getOptionType().getId()));
		checkState(option.getTariff().getId().equals(newOption.getTariff().getId()));
		return telephonyOptionServiceDtoTr.translate(option);
	}

	private boolean isAgencyContract() {
		return newContract.getContractType() != null && newContract.getContractType().getIdentifiable()
				.getContractCategory().equals(ContractCategory.AGENCY);
	}

	private void setDefaultBroker() {
		newContract.setBroker(businessObjectDtoTr.translate(contractAs.findDefaultBroker()));
	}

	private void refreshPossibleBrokers() {
		possibleBrokers = businessObjectDtoTr.translate(contractAs.findPossibleBrokers());
	}

	public enum Step {
		OPTION_CREATION("create_option"), CONTRACT_CREATION("create_contract");

		@Getter
		private String id;

		Step(String id) {
			this.id = id;
		}

		public static Step getStep(String id) {
			return Arrays.stream(values()).filter(step -> step.getId().equals(id)).findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Unknown step id"));
		}
	}
}
