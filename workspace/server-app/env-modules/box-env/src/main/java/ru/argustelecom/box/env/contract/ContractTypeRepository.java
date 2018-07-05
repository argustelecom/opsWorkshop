package ru.argustelecom.box.env.contract;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.contract.model.ContractExtensionType.ContractExtensionTypeQuery;
import ru.argustelecom.box.env.contract.model.ContractRoleType;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.contract.model.ContractType.ContractTypeQuery;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class ContractTypeRepository implements Serializable {

	private static final long serialVersionUID = 6381309249365639269L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TypeFactory typeFactory;

	@Inject
	private ContractRoleRulesRepository rulesRp;

	public ContractType createContractType(CustomerType customerType, String name, String description, String keyword,
			ContractCategory contractCategory, PartyRole provider) {
		checkRequiredArgument(customerType, "CustomerType");
		checkRequiredArgument(name, "Name");
		checkRequiredArgument(contractCategory, "ContractCategory");
		checkRequiredArgument(provider, "Provider");
		checkArgument(rulesRp.checkPartyClass(contractCategory, ContractRoleType.PROVIDER, provider));

		ContractType result = typeFactory.createType(ContractType.class);
		result.setName(name);
		result.setDescription(description);
		result.setKeyword(keyword);
		result.setCustomerType(customerType);
		result.setContractCategory(contractCategory);
		result.setProvider(provider);

		em.persist(result);
		em.flush();
		return result;
	}

	public ContractExtensionType createExtensionType(CustomerType customerType, String name, String description,
			String keyword) {
		ContractExtensionType result = typeFactory.createType(ContractExtensionType.class);
		result.setName(name);
		result.setDescription(description);
		result.setCustomerType(customerType);
		result.setKeyword(keyword);

		em.persist(result);
		em.flush();
		return result;

	}

	public void saveContractType(ContractType contractType, CustomerType customerType, String name, String description,
			String keyword) {
		checkRequiredArgument(customerType, "CustomerType");
		checkRequiredArgument(name, "Name");

		contractType.setName(name);
		contractType.setDescription(description);
		contractType.setKeyword(keyword);
		contractType.setCustomerType(customerType);
	}

	public void saveContractExtensionType(ContractExtensionType contractExtensionType, CustomerType customerType,
			String name, String description, String keyword) {

		contractExtensionType.setName(name);
		contractExtensionType.setDescription(description);
		contractExtensionType.setCustomerType(customerType);
		contractExtensionType.setKeyword(keyword);
	}

	public List<ContractType> findAllContractTypes() {
		return new ContractTypeQuery<>(ContractType.class).getResultList(em);
	}

	public List<ContractType> findContractTypes(CustomerType customerType) {
		checkRequiredArgument(customerType, "CustomerType");

		ContractTypeQuery<ContractType> query = new ContractTypeQuery<>(ContractType.class);
		query.and(query.customerType().equal(customerType));
		return query.getResultList(em);
	}

	public List<ContractType> findContractTypes(PartyRole provider) {
		checkRequiredArgument(provider, "Provider");

		ContractTypeQuery<ContractType> query = new ContractTypeQuery<>(ContractType.class);
		query.and(query.provider().equal(provider));
		return query.getResultList(em);
	}

	public boolean isAgencyContractTypesExists() {
		ContractTypeQuery<ContractType> query = new ContractTypeQuery<>(ContractType.class);
		return query.and(query.contractCategory().equal(ContractCategory.AGENCY)).calcRowsCount(em) > 0;
	}

	public List<ContractExtensionType> findAllExtensionTypes() {
		return new ContractExtensionTypeQuery<>(ContractExtensionType.class).getResultList(em);
	}

	public List<ContractExtensionType> findExtensionTypes(CustomerType customerType) {
		checkRequiredArgument(customerType, "CustomerType");

		ContractExtensionTypeQuery<ContractExtensionType> query = new ContractExtensionTypeQuery<>(
				ContractExtensionType.class);
		query.and(query.customerType().equal(customerType));
		return query.createTypedQuery(em).getResultList();
	}

	public List<ContractExtensionType> findCompatibleExtensionTypes(ContractType contractType) {
		checkRequiredArgument(contractType, "ContractType");

		return findExtensionTypes(contractType.getCustomerType());
	}

}