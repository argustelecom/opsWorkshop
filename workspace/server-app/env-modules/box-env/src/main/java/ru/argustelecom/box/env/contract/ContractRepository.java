package ru.argustelecom.box.env.contract;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.Contract.ContractQuery;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.model.ContractExtension.ContractExtensionQuery;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.contract.model.ContractRoleType;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.numerationpattern.NumberGenerator;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.contract.model.ContractState.CANCELLED;
import static ru.argustelecom.box.env.contract.model.ContractState.REGISTRATION;
import static ru.argustelecom.box.env.contract.model.ContractState.TERMINATED;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.findList;

@Repository
public class ContractRepository {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private NumberGenerator numberGenerator;

	@Inject
	private TypeFactory typeFactory;

	@Inject
	private ContractRoleRulesRepository rulesRp;

	public Contract createContract(ContractType type, Customer customer, String contractNumber) {
		checkRequiredArgument(type, "Type");
		checkArgument(type.getContractCategory().equals(ContractCategory.BILATERAL));

		return createContract(type, customer, contractNumber, null, null, null, null);
	}

	public Contract createContract(ContractType type, Customer customer, String contractNumber, Date validFrom,
			Date validTo, PaymentCondition paymentCondition, PartyRole broker) {
		checkRequiredArgument(type, "Type");
		checkRequiredArgument(customer, "Customer");
		checkRequiredArgument(validFrom, "ValidFrom");
		if (broker == null) {
			checkArgument(type.getContractCategory().equals(ContractCategory.BILATERAL));
		} else {
			checkArgument(type.getContractCategory().equals(ContractCategory.AGENCY));
			checkArgument(rulesRp.checkPartyClass(type.getContractCategory(), ContractRoleType.BROKER, broker));
		}

		Contract result = typeFactory.createInstance(type, Contract.class);

		result.setDocumentNumber(
				contractNumber == null ? numberGenerator.generateNumber(Contract.class, type) : contractNumber);
		result.setCustomer(customer);
		result.setState(REGISTRATION);
		result.setValidFrom(validFrom);
		result.setValidTo(validTo);
		result.setDocumentDate(validFrom);
		result.setPaymentCondition(paymentCondition);
		result.setBroker(broker);

		em.persist(result);
		return result;
	}

	public List<Contract> findAllContracts() {
		return new ContractQuery<>(Contract.class).getResultList(em);
	}

	public List<Contract> findContracts(Customer customer) {
		if (customer == null) {
			return emptyList();
		}

		ContractQuery<Contract> query = new ContractQuery<>(Contract.class);

		//@formatter:off
		query.and(
			query.customer().equal(customer), 
			query.state().notEqual(TERMINATED),
			query.state().notEqual(CANCELLED)
		);
		//@formatter:on

		return query.getResultList(em);
	}

	public List<Contract> findContractsInFormalization(Customer customer) {
		if (customer == null) {
			return emptyList();
		}

		ContractQuery<Contract> query = new ContractQuery<>(Contract.class);

		//@formatter:off
		query.and(
				query.customer().equal(customer),
				query.state().equal(REGISTRATION)
		);
		//@formatter:on

		return query.getResultList(em);
	}

	public List<Contract> findContracts(Customer customer, String contractNumber) {
		ContractQuery<Contract> query = new ContractQuery<>(Contract.class);

		//@formatter:off
		query.and(
			query.documentNumber().likeIgnoreCase(contractNumber),
			query.customer().equal(customer),
			query.state().notEqual(TERMINATED),
			query.state().notEqual(CANCELLED)
		);
		//@formatter:on

		return query.getResultList(em);
	}

	public List<Contract> findContracts(ContractCategory category) {
		ContractQuery<Contract> query = new ContractQuery<>(Contract.class);

		//@formatter:off
		query.and(
			query.category().equal(category),
			query.state().notEqual(TERMINATED),
			query.state().notEqual(CANCELLED)
		);
		//@formatter:on

		return query.getResultList(em);
	}

	public List<Contract> findContracts(String contractNumber, Customer customer, ContractState state, Date validFrom,
			Date validTo) {

		ContractQuery<Contract> query = new ContractQuery<>(Contract.class);

		//@formatter:off
		query.and(
			query.documentNumber().likeIgnoreCase(contractNumber),
			query.customer().equal(customer),
			query.state().equal(state),
			query.validFrom().greaterOrEqualTo(validFrom),
			query.validTo().lessOrEqualTo(validTo)
		);
		//@formatter:on

		return query.getResultList(em);
	}

	public ContractExtension createExtension(ContractExtensionType type, Contract contract, String number,
			Date extensionDate) {

		ContractExtension result = typeFactory.createInstance(type, ContractExtension.class);

		result.setState(REGISTRATION);
		result.setDocumentNumber(
				number == null ? numberGenerator.generateNumber(ContractExtension.class, type, contract) : number);
		result.setDocumentDate(extensionDate);

		contract.addExtension(result);
		em.persist(result);
		return result;
	}

	public List<ContractExtension> findExtensions(Contract contract) {
		if (contract == null) {
			return emptyList();
		}

		ContractExtensionQuery<ContractExtension> query = new ContractExtensionQuery<>(ContractExtension.class);
		query.and(query.contract().equal(contract));
		return query.getResultList(em);
	}

	public List<ContractExtension> findExtensions(Customer customer) {
		if (customer == null) {
			return emptyList();
		}

		ContractExtensionQuery<ContractExtension> query = new ContractExtensionQuery<>(ContractExtension.class);

		//@formatter:off
		query.and(
			query.customer().equal(customer), 
			query.state().notEqual(TERMINATED),
			query.state().notEqual(ContractState.CANCELLED)
		);
		//@formatter:on

		return query.getResultList(em);
	}

	private static final String COUNT_CONTRACTS_WITH_ENTRIES_WITHOUT_SUBS = "ContractRepository.countContractsWithEntriesWithoutSubs";

	//@formatter:off
	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = COUNT_CONTRACTS_WITH_ENTRIES_WITHOUT_SUBS, query =
			"SELECT count(c.id)\n" +
					"FROM system.contract c, system.contract_entry ce, system.product_offering po\n" +
					"WHERE c.id = ce.contract_id AND ce.product_offering_id = po.id AND\n" +
					"      (c.valid_to IS NULL OR c.valid_to >= now()) AND\n" +
					"      c.customer_id = :customerId AND\n" +
					"      po.dtype = 'PeriodProductOffering' AND\n" +
					"      NOT EXISTS(SELECT *\n" +
					"                 FROM SYSTEM.subscription_subject_cause sc\n" +
					"                 WHERE sc.contract_entry_id = ce.id)")
	//@formatter:on
	public long countContractsWithEntriesWithoutSubs(Long customerId) {
		List<BigInteger> count = em.createNamedQuery(COUNT_CONTRACTS_WITH_ENTRIES_WITHOUT_SUBS)
				.setParameter("customerId", customerId).getResultList();
		return count.get(0).longValue();
	}

	private static final String FIND_CONTRACTS_WITH_ENTRIES_WITHOUT_SUBS = "ContractRepository.findContractsWithEntriesWithoutSubs";

	//@formatter:off
	@SuppressWarnings({"unchecked", "rawtypes"})
	@NamedNativeQuery(name = FIND_CONTRACTS_WITH_ENTRIES_WITHOUT_SUBS, query =
			"SELECT DISTINCT c.id\n" +
					"FROM system.contract c, system.contract_entry ce, system.product_offering po\n" +
					"WHERE c.id = ce.contract_id AND ce.product_offering_id = po.id AND\n" +
					"      (c.valid_to IS NULL OR c.valid_to >= now()) AND\n" +
					"      c.customer_id = :customerId AND\n" +
					"      po.dtype = 'PeriodProductOffering' AND\n" +
					"      NOT EXISTS(SELECT *\n" +
					"                 FROM SYSTEM.subscription_subject_cause sc\n" +
					"                 WHERE sc.contract_entry_id = ce.id)")
	//@formatter:on
	public List<AbstractContract> findContractsWithEntriesWithoutSubs(Long customerId) {
		List<BigInteger> contractIdList = em.createNamedQuery(FIND_CONTRACTS_WITH_ENTRIES_WITHOUT_SUBS)
				.setParameter("customerId", customerId).getResultList();
		return findList(em, AbstractContract.class,
				contractIdList.stream().map(BigInteger::longValue).collect(toList()));
	}

	private static final String FIND_CONTRACT_ENTRIES_WITHOUT_SUBS = "ContractRepository.findContractEntriesWithoutSubs";

	//@formatter:off
	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = FIND_CONTRACT_ENTRIES_WITHOUT_SUBS, query =
				"SELECT ce.id\n" +
						"FROM system.contract_entry ce, system.product_offering po\n" +
						"WHERE ce.product_offering_id = po.id AND\n" +
						"      ce.contract_id = :contractId AND\n" +
						"      po.dtype = 'PeriodProductOffering' AND\n" +
						"      NOT EXISTS(SELECT *\n" +
						"                 FROM SYSTEM.subscription_subject_cause sc\n" +
						"                 WHERE sc.contract_entry_id = ce.id)")
	//@formatter:on
	public List<ContractEntry> findContractEntriesWithoutSubs(Long contractId) {
		List<BigInteger> entryIdList = em.createNamedQuery(FIND_CONTRACT_ENTRIES_WITHOUT_SUBS)
				.setParameter("contractId", contractId).getResultList();
		return findList(em, ContractEntry.class, entryIdList.stream().map(BigInteger::longValue).collect(toList()));
	}
}