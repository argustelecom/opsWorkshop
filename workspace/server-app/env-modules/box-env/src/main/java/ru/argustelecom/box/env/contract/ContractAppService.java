package ru.argustelecom.box.env.contract;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractRoleType;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.service.ApplicationService;

import static com.google.common.base.Preconditions.checkNotNull;

@ApplicationService
public class ContractAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ContractRepository contractRp;

	@Inject
	private ContractRoleRulesRepository rulesRp;

	public List<Contract> findContracts(Long customerId) {
		checkNotNull(customerId);
		return contractRp.findContracts(em.find(Customer.class, customerId));
	}

	public List<Contract> findContractsInFormalization(Long customerId) {
		checkNotNull(customerId);
		return contractRp.findContractsInFormalization(em.find(Customer.class, customerId));
	}

	public List<Contract> findContracts(Long customerId, String contractNumber) {
		return contractRp.findContracts(em.find(Customer.class, customerId), contractNumber);
	}

	public Contract createContract(Long typeId, Long customerId, String contractNumber, Date validFrom, Date validTo,
			PaymentCondition paymentCondition, Long brokerId) {
		ContractType type = em.getReference(ContractType.class, typeId);
		Customer customer = em.getReference(Customer.class, customerId);
		PartyRole broker = brokerId != null ? em.getReference(PartyRole.class, brokerId) : null;
		return contractRp.createContract(type, customer, contractNumber, validFrom, validTo, paymentCondition, broker);
	}


	public PartyRole findDefaultBroker() {
		return rulesRp.findDefaultParty(ContractCategory.AGENCY, ContractRoleType.BROKER);
	}

	public List<PartyRole> findPossibleBrokers() {
		return rulesRp.findPossibleParties(ContractCategory.AGENCY, ContractRoleType.BROKER);
	}

	public List<Contract> findContracts(ContractCategory category) {
		return contractRp.findContracts(category);
	}

	private static final long serialVersionUID = 8205476101419620352L;

}