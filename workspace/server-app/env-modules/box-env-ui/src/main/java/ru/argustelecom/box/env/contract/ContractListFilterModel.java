package ru.argustelecom.box.env.contract;

import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.BROKER;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.CONTRACT_TYPE;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.CUSTOMER;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.CUSTOMER_TYPE;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.NUMBER;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.PROVIDER;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.STATE;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.VALID_FROM;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.VALID_TO;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.contract.dto.ContractRoleDto;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.Contract.ContractQuery;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;

public class ContractListFilterModel extends BaseEQConvertibleDtoFilterModel<ContractQuery<Contract>> {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ContractListViewState contractListViewState;

	@Override
	public void buildPredicates(ContractQuery<Contract> query) {
		Map<String, Object> filterMap = contractListViewState.getFilterMap();
		for (Map.Entry<String, Object> filterEntry : filterMap.entrySet()) {
			if (filterEntry != null) {
				switch (filterEntry.getKey()) {
				case NUMBER:
					addPredicate(query.documentNumber().equal((String) filterEntry.getValue()));
					break;
				case CONTRACT_TYPE:
					addPredicate(query.type()
							.equal((ContractType) ((ContractTypeDto) filterEntry.getValue()).getIdentifiable(em)));
					break;
				case CUSTOMER_TYPE:
					addPredicate(query.byCustomerType(
							(CustomerType) ((CustomerTypeDto) filterEntry.getValue()).getIdentifiable(em)));
					break;
				case CUSTOMER:
					addPredicate(query.customer()
							.equal((Customer) ((CustomerDto) filterEntry.getValue()).getIdentifiable(em)));
					break;
				case STATE:
					addPredicate(query.state().equal((ContractState) filterEntry.getValue()));
					break;
				case VALID_FROM:
					addPredicate(query.validFrom().greaterOrEqualTo((Date) filterEntry.getValue()));
					break;
				case VALID_TO:
					addPredicate(query.validTo().lessOrEqualTo((Date) filterEntry.getValue()));
					break;
				case PROVIDER:
					addPredicate(
							query.byProvider((PartyRole) ((ContractRoleDto) filterEntry.getValue()).getIdentifiable()));
					break;
				case BROKER:
					addPredicate(query.broker()
							.equal((PartyRole) ((ContractRoleDto) filterEntry.getValue()).getIdentifiable()));
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public Supplier<ContractQuery<Contract>> entityQuerySupplier() {
		return () -> new ContractQuery<>(Contract.class);
	}

	private static final long serialVersionUID = -4756130339823323427L;
}
