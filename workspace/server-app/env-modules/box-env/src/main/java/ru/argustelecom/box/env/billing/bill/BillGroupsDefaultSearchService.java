package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static javax.persistence.TemporalType.TIMESTAMP;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static ru.argustelecom.box.env.billing.bill.BillGroupData.BILL_GROUP_DATA_MAPPER;
import static ru.argustelecom.box.env.billing.bill.BillGroupsDefaultSearchService.FIND_ALL_SHORT_TERM_INVOICES;
import static ru.argustelecom.box.env.billing.bill.BillGroupsDefaultSearchService.FIND_ALL_SUBSCRIPTIONS;
import static ru.argustelecom.box.env.billing.bill.BillGroupsDefaultSearchService.FIND_ALL_USAGE_INVOICES;
import static ru.argustelecom.box.env.billing.bill.BillGroupsDefaultSearchService.FIND_SHORT_TERM_INVOICES_BY_CUSTOMER_TYPE;
import static ru.argustelecom.box.env.billing.bill.BillGroupsDefaultSearchService.FIND_SUBSCRIPTIONS_BY_CUSTOMER_TYPE;
import static ru.argustelecom.box.env.billing.bill.BillGroupsDefaultSearchService.FIND_USAGE_INVOICES_BY_CUSTOMER_TYPE;
import static ru.argustelecom.box.env.billing.bill.model.GroupingMethod.CONTRACT;
import static ru.argustelecom.box.env.billing.bill.model.GroupingMethod.PERSONAL_ACCOUNT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.model.BillGroup;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQueries;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.exception.SystemException;

//@formatter:off
@NamedNativeQueries({
	@NamedNativeQuery(
		name = FIND_ALL_SUBSCRIPTIONS,
		resultSetMapping = BILL_GROUP_DATA_MAPPER,
		query = "WITH RECURSIVE all_contracts(contract_id, contract_parent_id, customer_id, provider_id, broker_id) AS (\n" +
				"  SELECT\n" +
				"    c.id,\n" +
				"    c.contract_id,\n" +
				"    c.customer_id,\n" +
				"    ct.provider_id,\n" +
				"    c.broker_id\n" +
				"  FROM\n" +
				"    system.contract c, system.contract_type ct\n" +
				"  WHERE c.contract_type_id = ct.id\n" +
				"        AND ct.provider_id IN (:provider_ids)\n" +
				"        AND c.dtype = 'Contract'\n" +
				"        AND c.payment_condition = :payment_condition\n" +
				"  UNION ALL\n" +
				"  SELECT\n" +
				"    ce.id,\n" +
				"    ce.contract_id,\n" +
				"    c.customer_id,\n" +
				"    c.provider_id,\n" +
				"    c.broker_id\n" +
				"  FROM\n" +
				"    all_contracts c,\n" +
				"    system.contract ce\n" +
				"  WHERE\n" +
				"    c.contract_id = ce.contract_id\n" +
				"    AND ce.dtype = 'ContractExtension'\n" +
				"    AND ce.state != 'REGISTRATION'\n" +
				")\n" +
				"SELECT\n" +
				"  s.id                          AS subject_id,\n" +
				"  CASE WHEN c.contract_parent_id IS NULL\n" +
				"    THEN c.contract_id\n" +
				"  ELSE c.contract_parent_id END AS contract_id,\n" +
				"  s.personal_account_id,\n" +
				"  c.provider_id,\n" +
				"  c.broker_id,\n" +
				"  c.customer_id,\n" +
				"  s.valid_from AS start,\n" +
				"  s.close_date AS end\n" +
				"FROM\n" +
				"  system.subscription s,\n" +
				"  system.subscription_subject_cause ssc,\n" +
				"  system.contract_entry ce,\n" +
				"  all_contracts c\n" +
				"WHERE s.subject_cause_id = ssc.id\n" +
				"      AND ssc.contract_entry_id = ce.id\n" +
				"      AND ce.contract_id = c.contract_id\n" +
				"      AND s.state != 'FORMALIZATION'\n" +
				"      AND s.valid_from <= :end_date\n" +
				"      AND (s.close_date >= :start_date OR s.close_date IS NULL)"
	),
	@NamedNativeQuery(
		name = FIND_SUBSCRIPTIONS_BY_CUSTOMER_TYPE,
		resultSetMapping = BILL_GROUP_DATA_MAPPER,
		query = "WITH RECURSIVE all_contracts(contract_id, contract_parent_id, customer_id, provider_id, broker_id) AS (\n" +
				"  SELECT\n" +
				"    c.id,\n" +
				"    c.contract_id,\n" +
				"    c.customer_id,\n" +
				"    ct.provider_id,\n" +
				"    c.broker_id\n" +
				"  FROM\n" +
				"    system.contract c, system.contract_type ct, (SELECT *\n" +
				"                                                 FROM system.customer_type_instance\n" +
				"                                                 WHERE customer_type_id = :customer_type_id) csi\n" +
				"  WHERE c.dtype = 'Contract'\n" +
				"        AND c.payment_condition = :payment_condition\n" +
				"        AND ct.provider_id IN (:provider_ids)\n" +
				"        AND c.contract_type_id = ct.id\n" +
				"  UNION ALL\n" +
				"  SELECT\n" +
				"    ce.id,\n" +
				"    ce.contract_id,\n" +
				"    c.customer_id,\n" +
				"    c.provider_id,\n" +
				"    c.broker_id\n" +
				"  FROM\n" +
				"    all_contracts c,\n" +
				"    system.contract ce\n" +
				"  WHERE c.contract_id = ce.contract_id AND ce.dtype = 'ContractExtension' AND ce.state != 'REGISTRATION'\n" +
				")\n" +
				"SELECT\n" +
				"  s.id                          AS subject_id,\n" +
				"  CASE WHEN c.contract_parent_id IS NULL\n" +
				"    THEN c.contract_id\n" +
				"  ELSE c.contract_parent_id END AS contract_id,\n" +
				"  s.personal_account_id,\n" +
				"  c.provider_id,\n" +
				"  c.broker_id,\n" +
				"  c.customer_id,\n" +
				"  s.valid_from AS start,\n" +
				"  s.close_date AS end\n" +
				"FROM\n" +
				"  system.subscription s,\n" +
				"  system.subscription_subject_cause ssc,\n" +
				"  system.contract_entry ce,\n" +
				"  all_contracts c\n" +
				"WHERE s.subject_cause_id = ssc.id\n" +
				"      AND ssc.contract_entry_id = ce.id\n" +
				"      AND ce.contract_id = c.contract_id\n" +
				"      AND s.state != 'FORMALIZATION'\n" +
				"      AND s.valid_from <= :end_date\n" +
				"      AND (s.close_date >= :start_date OR s.close_date IS NULL)"),
	@NamedNativeQuery(
			name = FIND_ALL_USAGE_INVOICES,
			resultSetMapping = BILL_GROUP_DATA_MAPPER,
			query = "SELECT\n" +
					"  i.id AS subject_id,\n" +
					"  CASE WHEN c.contract_id IS NULL\n" +
					"    THEN c.id\n" +
					"  ELSE c.contract_id END AS contract_id,\n" +
					"  i.personal_account_id,\n" +
					"  i.provider_id,\n" +
					"  c.broker_id,\n" +
					"  c.customer_id,\n" +
					"  i.end_date AS start,\n" +
					"  i.end_date AS end\n" +
					"FROM\n" +
					"  system.invoice i\n" +
					"  JOIN system.commodity o ON i.option_id = o.id\n" +
					"  JOIN system.contract_entry ce ON o.option_subject_id = ce.id\n" +
					"  JOIN system.contract c ON ce.contract_id = c.id\n" +
					"WHERE i.dtype = 'UsageInvoice'\n" +
					"      AND i.state = 'CLOSED'\n" +
					"      AND (i.end_date >= :start_date AND i.end_date <= :end_date)\n" +
					"	   AND i.provider_id IN (:provider_ids)"
		),
	@NamedNativeQuery(
			name = FIND_USAGE_INVOICES_BY_CUSTOMER_TYPE,
			resultSetMapping = BILL_GROUP_DATA_MAPPER,
			query = "SELECT\n" +
					"  i.id AS subject_id,\n" +
					"  CASE WHEN c.contract_id IS NULL\n" +
					"    THEN c.id\n" +
					"  ELSE c.contract_id END AS contract_id,\n" +
					"  i.personal_account_id,\n" +
					"  i.provider_id,\n" +
					"  c.broker_id,\n" +
					"  c.customer_id,\n" +
					"  i.end_date AS start,\n" +
					"  i.end_date AS end\n" +
					"FROM\n" +
					"  system.invoice i\n" +
					"  JOIN system.commodity o ON i.option_id = o.id\n" +
					"  JOIN system.contract_entry ce ON o.option_subject_id = ce.id\n" +
					"  JOIN system.contract c ON ce.contract_id = c.id\n" +
					"  JOIN system.contract_type ct ON c.contract_type_id = ct.id\n" +
					"WHERE i.dtype = 'UsageInvoice'\n" +
					"      AND i.state = 'CLOSED'\n" +
					"      AND (i.end_date >= :start_date AND i.end_date <= :end_date)\n" +
					"	   AND i.provider_id IN (:provider_ids)\n" +
					"	   AND ct.customer_type_id = :customer_type_id"
		),
	@NamedNativeQuery(
			name = FIND_ALL_SHORT_TERM_INVOICES,
			resultSetMapping = BILL_GROUP_DATA_MAPPER,
			query = "SELECT\n" +
					"  i.id AS subject_id,\n" +
					"  NULL as contract_id,\n" +
					"  i.personal_account_id,\n" +
					"  p.owner_id as provider_id,\n" +
					"  NULL as broker_id,\n" +
					"  pa.customer_id,\n" +
					"  i.closing_date AS start,\n" +
					"  i.closing_date AS end\n" +
					"FROM\n" +
					"  system.invoice i\n" +
					"  JOIN system.personal_account pa ON i.personal_account_id = pa.id\n" +
					"  JOIN system.invoice_entry ie ON i.id = ie.invoice_id\n" +
					"  JOIN system.product_offering po ON ie.product_offering_id = po.id\n" +
					"  JOIN system.pricelist p ON p.id = po.pricelist_id\n" +
					"  JOIN system.customer c ON pa.customer_id = c.id\n" +
					"WHERE i.dtype = 'ShortTermInvoice'\n" +
					"      AND (i.closing_date >= :start_date AND i.closing_date <= :end_date)"
		),
	@NamedNativeQuery(
			name = FIND_SHORT_TERM_INVOICES_BY_CUSTOMER_TYPE,
			resultSetMapping = BILL_GROUP_DATA_MAPPER,
			query = "SELECT\n" +
					"  i.id AS subject_id,\n" +
					"  NULL as contract_id,\n" +
					"  i.personal_account_id,\n" +
					"  p.owner_id as provider_id,\n" +
					"  NULL as broker_id,\n" +
					"  pa.customer_id,\n" +
					"  i.closing_date AS start,\n" +
					"  i.closing_date AS end\n" +
					"FROM\n" +
					"  system.invoice i\n" +
					"  JOIN system.personal_account pa ON i.personal_account_id = pa.id\n" +
					"  JOIN system.invoice_entry ie ON i.id = ie.invoice_id\n" +
					"  JOIN system.product_offering po ON ie.product_offering_id = po.id\n" +
					"  JOIN system.pricelist p ON p.id = po.pricelist_id\n" +
					"  JOIN system.customer c ON pa.customer_id = c.id\n" +
					"  JOIN system.customer_type_instance cti ON c.type_instance_id = cti.id\n" +
					"WHERE i.dtype = 'ShortTermInvoice'\n" +
					"	   AND cti.customer_type_id = :customer_type_id" +
					"      AND (i.closing_date >= :start_date AND i.closing_date <= :end_date)\n"
		)
})
//@formatter:on
public abstract class BillGroupsDefaultSearchService implements BillGroupsSearchService {

	final static String FIND_ALL_SUBSCRIPTIONS = "BillGroupsDefaultSearchService.findAllSubscriptions";
	final static String FIND_SUBSCRIPTIONS_BY_CUSTOMER_TYPE = "BillGroupsDefaultSearchService.findSubscriptionsByCustomerType";
	final static String FIND_ALL_USAGE_INVOICES = "BillGroupsDefaultSearchService.findAllUsageInvoices";
	final static String FIND_USAGE_INVOICES_BY_CUSTOMER_TYPE = "BillGroupsDefaultSearchService.findUsageInvoicesByCustomerType";
	final static String FIND_ALL_SHORT_TERM_INVOICES = "BillGroupsDefaultSearchService.findAllShortTermInvoices";
	final static String FIND_SHORT_TERM_INVOICES_BY_CUSTOMER_TYPE = "BillGroupsDefaultSearchService.findShortTermInvoicesByCustomerType";

	@PersistenceContext
	protected EntityManager em;

	/**
	 * Если в метод передан тип клиента, то выполняется поиск через
	 * {@link #findSubscriptionsByCustomerType(GroupingMethod, Date, Date, PaymentCondition, CustomerType, Set)} иначе
	 * {@link #findAllSubscriptions(GroupingMethod, Date, Date, PaymentCondition, Set)}
	 */
	List<BillGroupData> findAllSubscriptionsOrByCustomerType(GroupingMethod groupingMethod, Date start, Date end,
			PaymentCondition paymentCondition, CustomerType customerType, Set<Long> providerIds) {
		if (customerType != null) {
			return findSubscriptionsByCustomerType(groupingMethod, start, end, paymentCondition, customerType,
					providerIds);
		} else {
			return findAllSubscriptions(groupingMethod, start, end, paymentCondition, providerIds);
		}
	}

	/**
	 * Если в метод передан тип клиента, то выполняется поиск через
	 * {@link #findUsageInvoicesByCustomerType(GroupingMethod, Date, Date, PaymentCondition, CustomerType, Set)} иначе
	 * {@link #findAllUsageInvoices(GroupingMethod, Date, Date, PaymentCondition, Set)}
	 */
	List<BillGroupData> findAllUsageInvoicesOrByCustomerType(GroupingMethod groupingMethod, Date start, Date end,
			PaymentCondition paymentCondition, CustomerType customerType, Set<Long> providerIds) {
		if (customerType != null) {
			return findUsageInvoicesByCustomerType(groupingMethod, start, end, paymentCondition, customerType,
					providerIds);
		} else {
			return findAllUsageInvoices(groupingMethod, start, end, paymentCondition, providerIds);
		}
	}

	/**
	 * Если в метод передан тип клиента, то выполняется поиск через
	 * {@link #findShortTermInvoicesByCustomerType(GroupingMethod, Date, Date, PaymentCondition, CustomerType, Set)}
	 * иначе {@link #findAllShortTermInvoices(GroupingMethod, Date, Date, PaymentCondition, Set)}
	 */
	List<BillGroupData> findAllShortTermInvoicesOrByCustomerType(GroupingMethod groupingMethod, Date start, Date end,
			PaymentCondition paymentCondition, CustomerType customerType, Set<Long> providerIds) {
		if (customerType != null) {
			return findShortTermInvoicesByCustomerType(groupingMethod, start, end, paymentCondition, customerType,
					providerIds);
		} else {
			return findAllShortTermInvoices(groupingMethod, start, end, paymentCondition, providerIds);
		}
	}

	/**
	 * Ищет все подписки входящие в интервал дат.
	 */
	private List<BillGroupData> findAllSubscriptions(GroupingMethod groupingMethod, Date start, Date end,
			PaymentCondition paymentCondition, Set<Long> providerIds) {
		checkNotNull(groupingMethod);
		checkNotNull(start);
		checkNotNull(end);
		checkArgument(!isEmpty(providerIds));

		//@formatter:off
		return em.createNamedQuery(FIND_ALL_SUBSCRIPTIONS, BillGroupData.class)
				.setParameter("payment_condition", paymentCondition.name())
				.setParameter("provider_ids", providerIds)
				.setParameter("start_date", start, TIMESTAMP)
				.setParameter("end_date", end, TIMESTAMP)
			.getResultList();
		//@formatter:on
	}

	/**
	 * Ищет все подписки для конкретного типа клиента, входящие в интервал дат.
	 */
	private List<BillGroupData> findSubscriptionsByCustomerType(GroupingMethod groupingMethod, Date start, Date end,
			PaymentCondition paymentCondition, CustomerType customerType, Set<Long> providerIds) {
		checkNotNull(groupingMethod);
		checkNotNull(start);
		checkNotNull(end);
		checkNotNull(customerType);
		checkArgument(!isEmpty(providerIds));

		//@formatter:off
		return em.createNamedQuery(FIND_SUBSCRIPTIONS_BY_CUSTOMER_TYPE, BillGroupData.class)
				.setParameter("payment_condition", paymentCondition.name())
				.setParameter("provider_ids", providerIds)
				.setParameter("start_date", start, TIMESTAMP)
				.setParameter("end_date", end, TIMESTAMP)
				.setParameter("customer_type_id", customerType.getId())
			.getResultList();
		//@formatter:on
	}

	/**
	 * Ищет все инвойсы по фактам использования входящие в интервал дат.
	 */
	private List<BillGroupData> findAllUsageInvoices(GroupingMethod groupingMethod, Date start, Date end,
			PaymentCondition paymentCondition, Set<Long> providerIds) {
		checkNotNull(groupingMethod);
		checkNotNull(start);
		checkNotNull(end);
		checkArgument(!isEmpty(providerIds));

		//@formatter:off
		return em.createNamedQuery(FIND_ALL_USAGE_INVOICES, BillGroupData.class)
				.setParameter("provider_ids", providerIds)
				.setParameter("start_date", start, TIMESTAMP)
				.setParameter("end_date", end, TIMESTAMP)
			.getResultList();
		//@formatter:on
	}

	/**
	 * Ищет все инвойсы по фактам использования для конкретного типа клиента, входящие в интервал дат.
	 */
	private List<BillGroupData> findUsageInvoicesByCustomerType(GroupingMethod groupingMethod, Date start, Date end,
			PaymentCondition paymentCondition, CustomerType customerType, Set<Long> providerIds) {
		checkNotNull(groupingMethod);
		checkNotNull(start);
		checkNotNull(end);
		checkNotNull(customerType);
		checkArgument(!isEmpty(providerIds));

		//@formatter:off
		return em.createNamedQuery(FIND_USAGE_INVOICES_BY_CUSTOMER_TYPE, BillGroupData.class)
				.setParameter("provider_ids", providerIds)
				.setParameter("start_date", start, TIMESTAMP)
				.setParameter("end_date", end, TIMESTAMP)
				.setParameter("customer_type_id", customerType.getId())
			.getResultList();
		//@formatter:on
	}

	/**
	 * Ищет все инвойсы по единовременным услугам входящие в интервал дат.
	 */
	private List<BillGroupData> findAllShortTermInvoices(GroupingMethod groupingMethod, Date start, Date end,
			PaymentCondition paymentCondition, Set<Long> providerIds) {
		checkNotNull(start);
		checkNotNull(end);

		//@formatter:off
		return em.createNamedQuery(FIND_ALL_SHORT_TERM_INVOICES, BillGroupData.class)
				.setParameter("start_date", start, TIMESTAMP)
				.setParameter("end_date", end, TIMESTAMP)
			.getResultList();
		//@formatter:on
	}

	/**
	 * Ищет все инвойсы по единовременным услугам для конкретного типа клиента, входящие в интервал дат.
	 */
	private List<BillGroupData> findShortTermInvoicesByCustomerType(GroupingMethod groupingMethod, Date start, Date end,
			PaymentCondition paymentCondition, CustomerType customerType, Set<Long> providerIds) {
		checkNotNull(start);
		checkNotNull(end);
		checkNotNull(customerType);
		//@formatter:off
		return em.createNamedQuery(FIND_SHORT_TERM_INVOICES_BY_CUSTOMER_TYPE, BillGroupData.class)
				.setParameter("start_date", start, TIMESTAMP)
				.setParameter("end_date", end, TIMESTAMP)
				.setParameter("customer_type_id", customerType.getId())
			.getResultList();
		//@formatter:on
	}

	/**
	 * Создает список {@link BillGroup} если хотя бы один элемент groupData пересекается с периодом счета, иначе возвращает
	 * пустой список
	 * 
	 * @param groupingMethod
	 *            способ группировки подписок.
	 * @param groupData
	 *            данные для группировки
	 * @param billPeriod
	 *            период счета
	 */
	List<BillGroup> createGroupsIfNeed(GroupingMethod groupingMethod, List<BillGroupData> groupData,
			BillPeriod billPeriod) {
		if (groupData.stream()
				.anyMatch(gd -> !DateUtils.before(billPeriod.endDate(), gd.getStart()) && ((gd.getEnd() == null)
						|| !DateUtils.after(billPeriod.startDate(), gd.getEnd())))) {
			return collectToGroups(groupingMethod, groupData);
		}

		return new ArrayList<>();
	}

	/**
	 * Собирает результат выборки данных, представленный в виде {@link BillGroupData в группы {@link BillGroup}
	 * 
	 * @param groupingMethod
	 *            способ группировки подписок.
	 */
	private List<BillGroup> collectToGroups(GroupingMethod groupingMethod, List<BillGroupData> groupData) {
		switch (groupingMethod) {
		case CONTRACT:
			return groupByContract(groupData);
		case PERSONAL_ACCOUNT:
			return groupByPersonalAccount(groupData);
		default:
			throw new SystemException(String.format("Не поддерживаемый тип группировки: %s", groupingMethod));

		}
	}

	private List<BillGroup> groupByContract(List<BillGroupData> groupData) {
		Map<Long, List<BillGroupData>> groupDataMap = groupData.stream()
				.collect(groupingBy(BillGroupData::getContractId));

		return groupDataMap.entrySet().stream().map(gd -> {
			Long groupId = gd.getKey();
			Long providerId = gd.getValue().get(0).getProviderId();
			Long brokerId = gd.getValue().get(0).getBrokerId();
			Long customerId = gd.getValue().get(0).getCustomerId();
			return createBillGroup(groupId, CONTRACT, providerId, brokerId, customerId, gd.getValue());
		}).collect(toList());
	}

	private List<BillGroup> groupByPersonalAccount(List<BillGroupData> groupData) {
		Map<BillGroupData.PersonalAccountBillGroupKey, List<BillGroupData>> groupDataMap = groupData.stream()
				.collect(groupingBy(BillGroupData::getPersonalAccountBillGroupKey));

		return groupDataMap.entrySet().stream().map(gd -> {
			BillGroupData.PersonalAccountBillGroupKey key = gd.getKey();
			Long groupId = key.getPersonalAccountId();
			Long providerId = key.getProviderId();
			Long brokerId = key.getBrokerId();
			Long customerId = gd.getValue().get(0).getCustomerId();

			return createBillGroup(groupId, PERSONAL_ACCOUNT, providerId, brokerId, customerId, gd.getValue());
		}).collect(toList());
	}

	private BillGroup createBillGroup(Long groupId, GroupingMethod groupingMethod, Long providerId, Long brokerId,
			Long customerId, List<BillGroupData> elements) {
		List<Long> subsIds = new ArrayList<>();
		List<Long> usageInvoiceIds = new ArrayList<>();
		List<Long> shortTermInvoiceIds = new ArrayList<>();

		elements.forEach(element -> {
			switch (element.getChargesType()) {
			case RECURRENT:
				subsIds.add(element.getSubjectId());
				break;
			case USAGE:
				usageInvoiceIds.add(element.getSubjectId());
				break;
			case NONRECURRENT:
				shortTermInvoiceIds.add(element.getSubjectId());
				break;
			default:
				throw new SystemException("Unknown chargesType");
			}
		});

		return new BillGroup(groupId, groupingMethod, providerId, brokerId, customerId, subsIds, usageInvoiceIds,
				shortTermInvoiceIds);
	}

}