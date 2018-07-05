package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.billing.bill.model.GroupingMethod.CONTRACT;
import static ru.argustelecom.box.env.billing.bill.model.GroupingMethod.PERSONAL_ACCOUNT;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.Bill.BillQuery;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.Bill_;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.numerationpattern.NumberGenerator;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

/**
 * Репозиторий для работы со счетами на оплату.
 */
@Repository
public class BillRepository implements Serializable {

	private static final long serialVersionUID = 5489123250303384172L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService iss;

	@Inject
	private NumberGenerator numberGenerator;

	@Inject
	private BillRawDataRepository billRawDataRepository;

	@Inject
	private TypeFactory typeFactory;

	private final static String LAST_BILL_BY_CUSTOMER_QUERY = "BillRepository.lastBillByCustomerQuery";

	/**
	 * Создание счёта, без расчёта итоговых сумм к оплате.
	 */
	public Bill create(Long id, BillType billType, String number, Customer customer, GroupingMethod groupingMethod,
			Long groupId, PaymentCondition paymentCondition, BillPeriodType periodType, BillPeriod period,
			Date billDate, ReportModelTemplate template, DataHolder dataHolder, PartyRole provider, Owner broker) {

		checkNotNull(billType);
		checkNotNull(customer);
		checkNotNull(groupingMethod);
		checkNotNull(groupId);
		checkNotNull(paymentCondition);
		checkNotNull(periodType);
		checkNotNull(period);
		checkNotNull(billDate);
		checkNotNull(dataHolder);
		checkNotNull(provider);

		Long billId = id != null ? id : iss.nextValue(Bill.class);
		Bill result = typeFactory.createInstance(billType, Bill.class, billId);

		result.setDocumentNumber(number == null ? numberGenerator.generateNumber(Bill.class, billType) : number);
		result.setPeriodType(periodType);
		result.setDocumentDate(billDate);
		result.setPeriodUnit(period.getPeriodUnit());
		result.setStartDate(period.startDate());
		result.setEndDate(period.endDate());
		result.setCustomer(customer);
		result.setGroupingMethod(groupingMethod);
		result.setGroupId(groupId);
		result.setPaymentCondition(paymentCondition);
		result.setTemplate(template);
		result.changeData(billRawDataRepository.create(dataHolder.getRawDataHolder()), dataHolder.getAggDataHolder());
		result.setProvider(provider);
		result.setBroker(broker);

		em.persist(result);

		return result;
	}

	/**
	 * Возвращает список всех счетов.
	 */
	public List<Bill> findAll() {
		return new BillQuery<>(Bill.class).getResultList(em);
	}

	/**
	 * Возращает список всех счетов для договора.
	 */
	public List<Bill> findByContract(Long contractId) {
		BillQuery<Bill> query = new BillQuery<>(Bill.class);
		query.and(query.groupingMethod().equal(CONTRACT)).and(query.groupId().equal(contractId));
		return query.getResultList(em);
	}

	/**
	 * Возращает список всех счетов для лицевого счёта.
	 */
	public List<Bill> findByPersonalAccount(Long personalAccountId) {
		BillQuery<Bill> query = new BillQuery<>(Bill.class);
		query.and(query.groupingMethod().equal(PERSONAL_ACCOUNT)).and(query.groupId().equal(personalAccountId));
		return query.createTypedQuery(em).getResultList();
	}

	/**
	 * Возращает список всех счетов для клиента.
	 */
	public List<Bill> findByCustomer(Customer customer) {
		BillQuery<Bill> query = new BillQuery<>(Bill.class);
		query.and(query.customer().equal(customer));
		return query.createTypedQuery(em).getResultList();
	}

	public boolean isBillAlreadyExists(GroupingMethod groupingMethod, Long groupId, PaymentCondition paymentCondition,
			Date startDate, Date endDate) {

		BillQuery<Bill> query = new BillQuery<>(Bill.class);

		//@formatter:off
		query.and(
			query.groupingMethod().equal(groupingMethod),
			query.groupId().equal(groupId),
			query.paymentCondition().equal(paymentCondition),
			query.endDate().equal(endDate),
			query.startDate().equal(startDate)
		);
		//@formatter:on

		return query.getFirstResult(em) != null;
	}

	@NamedQuery(name = LAST_BILL_BY_CUSTOMER_QUERY, query = "from Bill where customer = :customer and "
			+ "documentDate = (select max(documentDate) from Bill where customer = :customer)")
	public Bill findLastBillByCustomer(Customer customer) {
		checkArgument(customer != null);

		TypedQuery<Bill> query = em.createNamedQuery(LAST_BILL_BY_CUSTOMER_QUERY, Bill.class);
		query.setParameter("customer", customer);
		return query.getResultList().stream().findFirst().orElse(null);
	}

	public List<Bill> findBills(List<Long> groupIds, GroupingMethod groupingMethod, Date startDate, Date endDate) {
		BillQuery<Bill> query = new BillQuery<>(Bill.class);

		//@formatter:off
		query.and(
			query.root().get(Bill_.groupId).in(groupIds),
			query.groupingMethod().equal(groupingMethod),
			query.startDate().equal(startDate),
			query.endDate().equal(endDate)
		);
		//@formatter:on

		return query.getResultList(em);
	}

	public List<Bill> findBills(BillType type, Date startDate, Date endDate) {
		checkRequiredArgument(type, "BillType");
		checkRequiredArgument(startDate, "StartDate");
		checkRequiredArgument(endDate, "EndDate");

		BillQuery<Bill> query = new BillQuery<>(Bill.class);
		//@formatter:off
		query.and(
				query.type().equal(type), 
				query.startDate().equal(startDate),
				query.endDate().equal(endDate)
		);
		//@formatter:on
		return query.getResultList(em);
	}

}