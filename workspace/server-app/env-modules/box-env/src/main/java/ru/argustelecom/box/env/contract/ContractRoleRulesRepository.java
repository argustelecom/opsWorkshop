package ru.argustelecom.box.env.contract;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractRole;
import ru.argustelecom.box.env.contract.model.ContractRoleRules;
import ru.argustelecom.box.env.contract.model.ContractRoleRules.ContractRoleRulesQuery;
import ru.argustelecom.box.env.contract.model.ContractRoleType;
import ru.argustelecom.box.env.party.OwnerRepository;
import ru.argustelecom.box.env.party.SupplierRepository;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@Repository
public class ContractRoleRulesRepository implements Serializable {

	private static final long serialVersionUID = 6067488218938671677L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private OwnerRepository ownerRp;

	@Inject
	private SupplierRepository supplierRp;

	public PartyRole findDefaultParty(ContractCategory contractCategory, ContractRoleType roleType) {
		ContractRoleRules rule = find(contractCategory, roleType);
		if (rule.getRole().equals(ContractRole.OWNER)) {
			return ownerRp.findPrincipal();
		}

		return null;
	}

	public List<PartyRole> findPossibleParties(ContractCategory contractCategory, ContractRoleType roleType) {
		ContractRoleRules rule = find(contractCategory, roleType);
		switch (rule.getRole()) {
		case OWNER:
			return ownerRp.findAll().stream().map(owner -> (PartyRole) owner).collect(Collectors.toList());
		case SUPPLIER:
			return supplierRp.findAll().stream().map(supplier -> (PartyRole) supplier).collect(Collectors.toList());
		case CUSTOMER:
			throw new SystemException("Getting a list of customers is not implemented");
		default:
			throw new SystemException("Unsupported role");
		}
	}

	public boolean checkPartyClass(ContractCategory contractCategory, ContractRoleType roleType, PartyRole party) {
		ContractRoleRules rule = find(contractCategory, roleType);
		PartyRole initializedProvider = EntityManagerUtils.initializeAndUnproxy(party);
		return rule.getRole().getPartyRoleClass().isAssignableFrom(initializedProvider.getClass());
	}

	private ContractRoleRules find(ContractCategory contractCategory, ContractRoleType roleType) {
		checkRequiredArgument(contractCategory, "ContractCategory");
		checkRequiredArgument(roleType, "RoleType");

		ContractRoleRulesQuery query = new ContractRoleRulesQuery();
		return query.and(query.contractCategory().equal(contractCategory)).and(query.roleType().equal(roleType))
				.getSingleResult(em);
	}

}
