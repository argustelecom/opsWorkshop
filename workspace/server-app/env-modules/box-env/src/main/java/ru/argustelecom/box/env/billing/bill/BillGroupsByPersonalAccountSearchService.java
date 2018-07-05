package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static javax.persistence.TemporalType.TIMESTAMP;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static ru.argustelecom.box.env.billing.bill.BillGroupData.BILL_GROUP_DATA_MAPPER;
import static ru.argustelecom.box.env.billing.bill.BillGroupsByPersonalAccountSearchService.FIND_SHORT_TERM_INVOICES;
import static ru.argustelecom.box.env.billing.bill.BillGroupsByPersonalAccountSearchService.FIND_SUBSCRIPTIONS;
import static ru.argustelecom.box.env.billing.bill.BillGroupsByPersonalAccountSearchService.FIND_USAGE_INVOICES;
import static ru.argustelecom.box.env.billing.bill.model.GroupingMethod.PERSONAL_ACCOUNT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.bill.model.BillGroup;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.ChargesType;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQueries;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

/**
 * Сервис ищет подписки по задданым параметрам (подробнее
 * {@linkplain BillGroupsSearchService#find(Date, Date, PaymentCondition, CustomerType, Long, Long, Set)} см.}). И
 * группирует найденные подписки по {@link ru.argustelecom.box.env.billing.account.model.PersonalAccount лицевым
 * счетам}.
 */
//@formatter:off
@NamedNativeQueries({
@NamedNativeQuery(
	name = FIND_SUBSCRIPTIONS,
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
			"  WHERE c.dtype = 'Contract'\n" +
			"        AND c.payment_condition = :payment_condition\n" +
			"        AND c.customer_id = :customer_id\n" +
			"        AND ct.provider_id IN (:provider_ids)\n" +
			"        AND c.contract_type_id = ct.id\n" +
			"  UNION ALL\n" +
			"  SELECT\n" +
			"    ce.id,\n" +
			"    ce.contract_id,\n" +
			"    ce.customer_id,\n" +
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
			"      AND s.personal_account_id = :personal_account_id\n" +
			"      AND s.state != 'FORMALIZATION'\n" +
			"      AND s.valid_from <= :end_date\n" +
			"      AND (s.close_date >= :start_date OR s.close_date IS NULL)"),
@NamedNativeQuery(
		name = FIND_USAGE_INVOICES,
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
				"WHERE i.personal_account_id = :personal_account_id\n" +
				"      AND i.dtype = 'UsageInvoice'\n" +
				"      AND i.state = 'CLOSED'\n" +
				"      AND (i.end_date >= :start_date AND i.end_date <= :end_date)\n" +
				"	   AND i.provider_id IN (:provider_ids)"),
@NamedNativeQuery(
		name = FIND_SHORT_TERM_INVOICES,
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
				"WHERE i.dtype = 'ShortTermInvoice'\n" +
				"	   AND i.personal_account_id = :personal_account_id\n" +
				"      AND (i.closing_date >= :start_date AND i.closing_date <= :end_date)")
})
//@formatter:on
@DomainService
@BillGroupsSearcher(PERSONAL_ACCOUNT)
public class BillGroupsByPersonalAccountSearchService extends BillGroupsDefaultSearchService {

	static final String FIND_SUBSCRIPTIONS = "BillGroupsByPersonalAccountSearchService.findSubscriptions";
	static final String FIND_USAGE_INVOICES = "BillGroupsByPersonalAccountSearchService.findUsageInvoices";
	static final String FIND_SHORT_TERM_INVOICES = "BillGroupsByPersonalAccountSearchService.findShortTermInvoices";

	@Override
	public List<BillGroup> find(BillPeriod billPeriod, Date start, Date end, PaymentCondition paymentCondition,
			CustomerType customerType, Long customerId, Long groupId, Set<Long> providerIds) {

		if (customerId == null && groupId != null) {
			customerId = em.find(PersonalAccount.class, groupId).getCustomer().getId();
		}

		List<BillGroupData> groupData = new ArrayList<>();
		groupData.addAll(
				findSubscriptions(start, end, paymentCondition, customerType, customerId, groupId, providerIds));
		if (paymentCondition.equals(PaymentCondition.POSTPAYMENT)) {
			groupData.addAll(
					findUsageInvoices(start, end, paymentCondition, customerType, customerId, groupId, providerIds));
			groupData.addAll(findShortTermInvoices(start, end, paymentCondition, customerType, customerId, groupId,
					providerIds));
		}

		return createGroupsIfNeed(PERSONAL_ACCOUNT, groupData, billPeriod);
	}

	private List<BillGroupData> findSubscriptions(Date start, Date end, PaymentCondition paymentCondition,
			CustomerType customerType, Long customerId, Long groupId, Set<Long> providerIds) {
		//@formatter:off
		List<BillGroupData> groupDataList = groupId != null 
				? findSubscriptionsByPersonalAccount(start, end, paymentCondition, customerId, groupId, providerIds)
				: findAllSubscriptionsOrByCustomerType(PERSONAL_ACCOUNT, start, end, paymentCondition, customerType, providerIds);
		//@formatter:on
		groupDataList.forEach(groupData -> groupData.setChargesType(ChargesType.RECURRENT));
		return groupDataList;
	}

	private List<BillGroupData> findSubscriptionsByPersonalAccount(Date start, Date end,
			PaymentCondition paymentCondition, Long customerId, Long groupId, Set<Long> providerIds) {
		checkNotNull(start);
		checkNotNull(end);
		checkNotNull(paymentCondition);
		checkNotNull(groupId);
		checkArgument(!isEmpty(providerIds));

		//@formatter:off
		return em.createNamedQuery(FIND_SUBSCRIPTIONS, BillGroupData.class)
				.setParameter("payment_condition", paymentCondition.name())
				.setParameter("provider_ids", providerIds)
				.setParameter("start_date", start, TIMESTAMP)
				.setParameter("end_date", end, TIMESTAMP)
				.setParameter("customer_id", customerId)
				.setParameter("personal_account_id", groupId)
			.getResultList();
		//@formatter:on
	}

	private List<BillGroupData> findUsageInvoices(Date start, Date end, PaymentCondition paymentCondition,
			CustomerType customerType, Long customerId, Long groupId, Set<Long> providerIds) {
		//@formatter:off
		List<BillGroupData> groupDataList = groupId != null 
				? findUsageInvoicesByPersonalAccount(start, end, groupId, providerIds)
				: findAllUsageInvoicesOrByCustomerType(PERSONAL_ACCOUNT, start, end, paymentCondition, customerType, providerIds);
		//@formatter:on
		groupDataList.forEach(groupData -> groupData.setChargesType(ChargesType.USAGE));
		return groupDataList;
	}

	private List<BillGroupData> findUsageInvoicesByPersonalAccount(Date start, Date end, Long groupId,
			Set<Long> providerIds) {
		checkNotNull(start);
		checkNotNull(end);
		checkNotNull(groupId);

		//@formatter:off
		return em.createNamedQuery(FIND_USAGE_INVOICES, BillGroupData.class)
				.setParameter("start_date", start, TIMESTAMP)
				.setParameter("end_date", end, TIMESTAMP)
				.setParameter("personal_account_id", groupId)
				.setParameter("provider_ids", providerIds)
			.getResultList();
		//@formatter:on
	}

	private List<BillGroupData> findShortTermInvoices(Date start, Date end, PaymentCondition paymentCondition,
			CustomerType customerType, Long customerId, Long groupId, Set<Long> providerIds) {
		//@formatter:off
		List<BillGroupData> groupDataList = groupId != null 
				? findShortTermInvoicesByPersonalAccount(start, end, groupId)
				: findAllShortTermInvoicesOrByCustomerType(PERSONAL_ACCOUNT, start, end, paymentCondition, customerType, providerIds);
		//@formatter:on
		groupDataList.forEach(groupData -> groupData.setChargesType(ChargesType.NONRECURRENT));
		return groupDataList;
	}

	private List<BillGroupData> findShortTermInvoicesByPersonalAccount(Date start, Date end, Long groupId) {
		checkNotNull(start);
		checkNotNull(end);
		checkNotNull(groupId);

		//@formatter:off
		return em.createNamedQuery(FIND_SHORT_TERM_INVOICES, BillGroupData.class)
				.setParameter("start_date", start, TIMESTAMP)
				.setParameter("end_date", end, TIMESTAMP)
				.setParameter("personal_account_id", groupId)
			.getResultList();
		//@formatter:on
	}

}