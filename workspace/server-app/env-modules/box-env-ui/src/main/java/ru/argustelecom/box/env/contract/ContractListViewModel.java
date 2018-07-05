package ru.argustelecom.box.env.contract;

import static ru.argustelecom.box.env.dto.DefaultDtoConverterUtils.translate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.Getter;
import ru.argustelecom.box.env.contract.dto.ContractRoleDto;
import ru.argustelecom.box.env.contract.dto.ContractRoleDtoTranslator;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerDtoTranslator;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.party.CustomerAppService;
import ru.argustelecom.box.env.party.CustomerTypeAppService;
import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class ContractListViewModel extends ViewModel {

	private static final long serialVersionUID = -8975799007666115381L;

	@Inject
	@Getter
	private ContractLazyDataModel lazyDm;

	@Inject
	private CustomerTypeAppService customerTypeAs;

	@Inject
	private OwnerAppService ownerAs;

	@Inject
	private CustomerAppService customerAs;

	@Inject
	private ContractAppService contractAs;

	@Inject
	private ContractTypeAppService contractTypeAs;

	@Inject
	private ContractListViewState contractListViewState;

	@Inject
	private CustomerTypeDtoTranslator customerTypeDtoTr;

	@Inject
	private ContractTypeDtoTranslator contractTypeDtoTr;

	@Inject
	private CustomerDtoTranslator customerDtoTr;

	@Inject
	private ContractRoleDtoTranslator contractRoleDtoTr;

	@Getter
	private List<ContractTypeDto> contractTypes;
	@Getter
	private List<ContractState> states;
	@Getter
	private List<CustomerTypeDto> customerTypes;
	@Getter
	private List<ContractRoleDto> providers;
	@Getter
	private List<ContractRoleDto> brokers;

	@Getter
	private boolean providerRendered;
	@Getter
	private boolean brokerRendered;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		brokerRendered = contractTypeAs.isAgencyContractTypesExists();
		providerRendered = brokerRendered || ownerAs.findAll().size() > 1;
		initLists();
	}

	private void initLists() {
		contractTypes = translate(contractTypeDtoTr, contractTypeAs.findAllContractTypes());
		customerTypes = translate(customerTypeDtoTr, customerTypeAs.findAllCustomerTypes());
		states = Arrays.asList(ContractState.values());
		if (brokerRendered) {
			brokers = contractRoleDtoTr.translate(contractAs.findPossibleBrokers());
		}
		if (providerRendered) {
			providers = contractRoleDtoTr.translate(contractTypeAs.findPossibleProviders(ContractCategory.BILATERAL));
			providers
					.addAll(contractRoleDtoTr.translate(contractTypeAs.findPossibleProviders(ContractCategory.AGENCY)));
		}
	}

	public List<CustomerDto> completeCustomer(String customerName) {
		if (contractListViewState.getCustomerType() != null)
			return customerAs.findCustomerBy(contractListViewState.getCustomerType().getId(), customerName).stream()
					.map(customerDtoTr::translate).collect(Collectors.toList());
		return Collections.emptyList();
	}

}