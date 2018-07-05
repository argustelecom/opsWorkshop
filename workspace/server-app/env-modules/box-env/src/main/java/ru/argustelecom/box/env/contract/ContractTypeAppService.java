package ru.argustelecom.box.env.contract;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.contract.model.ContractRoleType;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.inf.service.ApplicationService;

import static com.google.common.base.Preconditions.checkNotNull;

@ApplicationService
public class ContractTypeAppService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ContractTypeRepository contractTypeRp;

	@Inject
	private ContractRoleRulesRepository rulesRp;

	public ContractType createContractType(Long customerTypeId, String name, String description,
			ContractCategory contractCategory, Long partyRoleId) {
		CustomerType customerType = em.find(CustomerType.class, customerTypeId);
		PartyRole provider = em.find(PartyRole.class, partyRoleId);
		return contractTypeRp.createContractType(customerType, name, description, null, contractCategory, provider);
	}

	public ContractExtensionType createContractExtensionType(Long customerTypeId, String name, String description) {
		CustomerType customerType = em.find(CustomerType.class, customerTypeId);
		return contractTypeRp.createExtensionType(customerType, name, description, null);
	}

	public void saveContractType(Long contractTypeId, Long customerTypeId, String name, String description) {
		ContractType contractType = em.find(ContractType.class, contractTypeId);
		CustomerType customerType = em.find(CustomerType.class, customerTypeId);
		contractTypeRp.saveContractType(contractType, customerType, name, description, null);
	}

	public void saveContractExtensionType(Long contractExtensionTypeId, Long customerTypeId, String name,
			String description) {
		ContractExtensionType contractExtensionType = em.find(ContractExtensionType.class, contractExtensionTypeId);
		CustomerType customerType = em.find(CustomerType.class, customerTypeId);
		contractTypeRp.saveContractExtensionType(contractExtensionType, customerType, name, description, null);
	}

	public List<ContractType> findContractTypes(Long customerTypeId) {
		checkNotNull(customerTypeId);

		CustomerType customerType = em.find(CustomerType.class, customerTypeId);
		checkNotNull(customerType);

		return contractTypeRp.findContractTypes(customerType);
	}

	public List<ContractType> findAllContractTypes() {
		return contractTypeRp.findAllContractTypes();
	}

	public PartyRole findDefaultProvider(ContractCategory contractCategory) {
		return rulesRp.findDefaultParty(contractCategory, ContractRoleType.PROVIDER);
	}

	public List<PartyRole> findPossibleProviders(ContractCategory contractCategory) {
		return rulesRp.findPossibleParties(contractCategory, ContractRoleType.PROVIDER);
	}

	public boolean isAgencyContractTypesExists() {
		return contractTypeRp.isAgencyContractTypesExists();
	}

}
