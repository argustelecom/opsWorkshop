package ru.argustelecom.box.env.contract;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.criteria.JoinType;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.contract.ContractLazyDataModel.ContractSort;
import ru.argustelecom.box.env.contract.model.AbstractContractType_;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.Contract.ContractQuery;
import ru.argustelecom.box.env.contract.model.Contract_;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.CustomerTypeInstance_;
import ru.argustelecom.box.env.party.model.CustomerType_;
import ru.argustelecom.box.env.party.model.PartyRole_;
import ru.argustelecom.box.env.party.model.Party_;
import ru.argustelecom.box.env.party.model.role.Customer_;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ContractLazyDataModel
		extends EQConvertibleDtoLazyDataModel<Contract, ContractDto, ContractQuery<Contract>, ContractSort> {

	@Inject
	private ContractDtoTranslator contractDtoTranslator;

	@Inject
	private ContractListFilterModel contractListFilterModel;

	@PostConstruct
	private void postConstruct() {
		initPathMap();
	}

	private void initPathMap() {
		addPath(ContractSort.id, query -> query.root().get(Contract_.id));
		addPath(ContractSort.contractNumber, query -> query.root().get(Contract_.documentNumber));
		addPath(ContractSort.documentType,
				query -> query.root().join(Contract_.type, JoinType.LEFT).get(AbstractContractType_.name));
		addPath(ContractSort.customer, query -> query.root().join(Contract_.customer, JoinType.LEFT)
				.join(Customer_.party, JoinType.LEFT).get(Party_.sortName));
		addPath(ContractSort.customerType,
				query -> query.root().join(Contract_.customer, JoinType.LEFT)
						.join(Customer_.typeInstance, JoinType.LEFT).join(CustomerTypeInstance_.type, JoinType.LEFT)
						.get(CustomerType_.name));
		addPath(ContractSort.validFrom, query -> query.root().get(Contract_.validFrom));
		addPath(ContractSort.validTo, query -> query.root().get(Contract_.validTo));
		addPath(ContractSort.state, query -> query.root().get(Contract_.state));
		//FIXME сортировка по поставщику
		addPath(ContractSort.broker, query -> query.root().join(Contract_.broker, JoinType.LEFT)
				.join(PartyRole_.party, JoinType.LEFT).get(Party_.sortName));
	}

	@Override
	protected Class<ContractSort> getSortableEnum() {
		return ContractSort.class;
	}

	@Override
	protected DefaultDtoTranslator<ContractDto, Contract> getDtoTranslator() {
		return contractDtoTranslator;
	}

	@Override
	protected EQConvertibleDtoFilterModel<ContractQuery<Contract>> getFilterModel() {
		return contractListFilterModel;
	}

	public enum ContractSort {
		id, contractNumber, documentType, customer, customerType, validFrom, validTo, state, provider, broker;
	}

	private static final long serialVersionUID = 367476068329244480L;
}
