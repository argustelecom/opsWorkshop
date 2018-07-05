package ru.argustelecom.box.env.billing.bill;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.unmodifiableSet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.billing.bill.ChargesQueryResult.NONRECURRENT_QUERY_RESULT_MAPPER;
import static ru.argustelecom.box.env.billing.bill.ChargesQueryResult.USAGE_QUERY_RESULT_MAPPER;
import static ru.argustelecom.box.env.billing.bill.model.AnalyticTypeError.START_PERIOD_DATE_AFTER_END_DATE;
import static ru.argustelecom.box.env.billing.bill.model.BillAnalyticType.AnalyticCategory.CHARGE;
import static ru.argustelecom.box.env.billing.bill.model.ChargesType.RECURRENT;
import static ru.argustelecom.box.env.billing.bill.model.ChargesType.USAGE;
import static ru.argustelecom.box.env.contract.model.PaymentCondition.POSTPAYMENT;
import static ru.argustelecom.box.env.contract.model.PaymentCondition.PREPAYMENT;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;

import lombok.val;
import ru.argustelecom.box.env.billing.bill.model.Analytic;
import ru.argustelecom.box.env.billing.bill.model.AnalyticTypeError;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillDateGetter.BillPeriodDate;
import ru.argustelecom.box.env.billing.bill.model.ChargesRaw;
import ru.argustelecom.box.env.billing.bill.model.ChargesRawByNonRecurrent;
import ru.argustelecom.box.env.billing.bill.model.ChargesRawByRecurrent;
import ru.argustelecom.box.env.billing.bill.model.ChargesRawByUsage;
import ru.argustelecom.box.env.billing.bill.model.ChargesType;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.SubscriptionAccountingService;
import ru.argustelecom.box.env.billing.subscription.model.ContractSubjectCause;
import ru.argustelecom.box.env.billing.subscription.model.PricelistCostCause;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Сервис для расчёта сырых данных по начислениям.
 */
@DomainService
public class ChargesService extends RawDataService implements Serializable {

	public static Set<ChargesType> VALID_SUBSCRIPTION_CHARGES_TYPES = unmodifiableSet(newHashSet(RECURRENT, USAGE));

	@Inject
	private BillAnalyticTypeRepository analyticTypeRp;

	@Inject
	private SubscriptionAccountingService subscriptionAccountingSvc;

	@Inject
	private SubscriptionRepository subscriptionRp;

	@Inject
	private BillDateUtils billDateUtils;

	public List<ChargesRaw> initRawData(BillData billData) {
		List<Analytic> analytics = initChargesAnalytics(billData);

		Date minStartDate = billDateUtils.findMinStartDate(analytics);
		Date maxEndDate = billDateUtils.findMaxEndDate(analytics);

		List<ChargesRaw> chargesRawList = new ArrayList<>();

		Map<Subscription, List<InvoicePlan>> subsPlanMap = subscriptionAccountingSvc
				.calculateBillAccruals(billData.getSubscriptions(), minStartDate, maxEndDate, billData.getBillDate());
		for (Map.Entry<Subscription, List<InvoicePlan>> entry : subsPlanMap.entrySet()) {
			chargesRawList.addAll(
					createRawByRecurrent(analytics, entry.getKey(), entry.getValue(), billData.getPaymentCondition()));
		}
		chargesRawList.addAll(createRawByUsage(analytics, billData));
		// TODO: проверить, что нет пустой аналитики для расчёта начислений по единовременным инвойсам
		chargesRawList.addAll(createRawByNonRecurrent(analytics, billData));

		chargesRawList.sort(ChargesRaw.chargesRawComparator());

		return chargesRawList;
	}

	public List<Long> getSubscriptionIdList(List<ChargesRaw> chargesRawList) {
		Function<ChargesRaw, Long> subscriptionIdGetter = raw -> {
			ChargesType chargesType = raw.getChargesType();
			switch (chargesType) {
			case RECURRENT:
				return ((ChargesRawByRecurrent) raw).getSubscriptionId();
			case USAGE:
				val subject = em.find(Service.class, ((ChargesRawByUsage) raw).getServiceId()).getSubject();
				return subscriptionRp.findSubscription(subject).getId();
			default:
				throw new SystemException(format("Unsupported ChargesType: %s", chargesType));
			}
		};

		//@formatter:off
		return chargesRawList.stream()
				.filter(raw -> VALID_SUBSCRIPTION_CHARGES_TYPES.contains(raw.getChargesType()))
				.map(subscriptionIdGetter)
				.distinct()
				.collect(toList());
		//@formatter:on
	}

	/**
	 * Получает список всех типов аналитик для расчёта начислений и формирует для них даты, за которые они должны быть
	 * построины. Типы аналитик выбираются на основании спецификации счёта.
	 *
	 * @see ru.argustelecom.box.env.billing.bill.RawDataService.Analytic
	 */
	private List<Analytic> initChargesAnalytics(BillData billData) {
		BillPeriodDate periodDates = initBillDates(billData);
		Set<BillAnalyticType> chargesTypes = analyticTypeRp.find(billData.getBillType(), billData.getPeriod(), CHARGE);
		return createAnalytics(chargesTypes, periodDates);
	}

	/**
	 * Создание сырых данных на основании начислений по подписке. Хитрости:
	 * <ul>
	 * <li>если границы формирования типа аналитики {@link Analytic#getBoundaries()} не валидны, то добавляем данные с
	 * нулевым значением и ошибкой</li>
	 * <li>если для типа аналитики нет исходных данных, то необходимо добавить её с нулевым значением</li>
	 * </ul>
	 * 
	 * @param analytics
	 *            общий список типов аналитик, который нужно расчитать.
	 * @param subscription
	 *            подписка по которой расчитываются сырые данные.
	 * @param invoicePlans
	 *            инвойс план, на оснвоании которого расчитываются сырые данные.
	 * @param paymentCondition
	 *            условие оплаты.
	 */
	private List<ChargesRaw> createRawByRecurrent(List<Analytic> analytics, Subscription subscription,
			List<InvoicePlan> invoicePlans, PaymentCondition paymentCondition) {

		List<Analytic> analyticsForRecurrent = analytics.stream().filter(
				analytic -> !analytic.getType().getIsRow() || analytic.getType().getChargesType().equals(RECURRENT))
				.collect(toList());

		List<InvoicePlan> interestingPlans = filterInvoicePlans(invoicePlans, paymentCondition);

		List<ChargesRaw> chargesRawList = new ArrayList<>();

		analyticsForRecurrent.forEach(analytic -> {
			if (analytic.isValidBoundaries()) {
				interestingPlans.forEach(invoicePlan -> {
					//@formatter:off
					Optional<ChargesRaw> rawOptional = ofNullable(createRawByRecurrent(analytic, subscription, invoicePlan, paymentCondition));
					rawOptional.ifPresent(chargesRawList::add);
					//@formatter:on
				});
			} else {
				//@formatter:off
				chargesRawList.add(
					createInvalidOrEmptyRaw(
						analytic,
						subscription.getId(),
						subscription.getSubject().getId(),
						((PricelistCostCause) subscription.getCostCause()).getPricelist().getOwner().getTaxRate(),
						START_PERIOD_DATE_AFTER_END_DATE)
				);
				//@formatter:on
			}
		});

		// аналитики, по которым не нашлось данных надо добавить с нулевыми значениями
		List<Long> emptyAnalyticIds = analyticsForRecurrent.stream().map(analytic -> analytic.getType().getId())
				.collect(toList());
		List<Long> notEmptyAnalyticIds = chargesRawList.stream().map(ChargesRaw::getAnalyticTypeId).collect(toList());
		emptyAnalyticIds.removeAll(notEmptyAnalyticIds);

		emptyAnalyticIds.forEach(emptyAnalyticId -> {
			//@formatter:off
			Analytic emptyAnalytic = analyticsForRecurrent.stream().filter(a -> a.getType().getId().equals(emptyAnalyticId)).findFirst().orElse(null);
			chargesRawList.add(
				createInvalidOrEmptyRaw(
					emptyAnalytic,
					subscription.getId(),
					subscription.getSubject().getId(),
					((PricelistCostCause) subscription.getCostCause()).getPricelist().getOwner().getTaxRate(),
					null)
			);
			//@formatter:on
		});

		return chargesRawList;
	}

	/**
	 * Фильтрует список {@linkplain InvoicePlan инвойс планов}. Для разных {@linkplain PaymentCondition условий оплаты}
	 * нужно рассматривать только планы, за определённый промежуток времени:
	 * <ul>
	 * <li><b>Постоплата</b> - только заверщенные инвойс планы.</li>
	 * <li><b>Предоплата</b> - все инвойс планы.</li>
	 * </ul>
	 */
	private List<InvoicePlan> filterInvoicePlans(List<InvoicePlan> invoicePlans, PaymentCondition paymentCondition) {
		if (POSTPAYMENT.equals(paymentCondition)) {
			return invoicePlans.stream().filter(InvoicePlan::isPast).collect(toList());
		}
		return invoicePlans;
	}

	/**
	 * Создание сырых данных на основании начислений по разовым продуктам/услугам. Данные выбираются одним запросом, за
	 * общий период для всех аналитик. Хитрости:
	 * <ul>
	 * <li>если границы формирования типа аналитики {@link Analytic#getBoundaries()} не валидны, то такой тип аналитики
	 * не обрабатывается</li>
	 * <li>если для типа аналитики нет исходных данных, то такой тип аналитики не обрабатывается</li>
	 * <li>для данного типа сырых данных включать только инвойсы строго входящие в период</li>
	 * </ul>
	 * 
	 * @param analytics
	 *            общий список типов аналитик, который нужно расчитать.
	 * @param billData
	 *
	 */
	private List<ChargesRaw> createRawByNonRecurrent(List<Analytic> analytics, BillData billData) {

		List<Analytic> analyticsForNonRecurrent = analytics.stream().filter(analytic -> !analytic.getType().getIsRow()
				|| analytic.getType().getChargesType().equals(ChargesType.NONRECURRENT)).collect(toList());

		List<ChargesQueryResult> results = findShortTermInvoices(billData);

		List<ChargesRaw> chargesRawList = new ArrayList<>();

		analyticsForNonRecurrent.forEach(analytic -> {
			if (analytic.isValidBoundaries()) {
				results.forEach(raw -> {
					boolean containsStartDate = analytic.getBoundaries().contains(raw.getStartDate());
					boolean containsEndDate = analytic.getBoundaries().contains(raw.getEndDate());
					if (containsStartDate && containsEndDate) {
						//@formatter:off
						ChargesRaw chargesRaw = ChargesRawByNonRecurrent.builder()
													.analyticTypeId(analytic.getType().getId())
													.productId(raw.getSubjectId())
													.taxRate(convertTax(raw.getTaxRate()))
													.startDate(raw.getStartDate())
													.endDate(raw.getEndDate())
													.sum(raw.getSum())
													.row(analytic.getType().getIsRow())
												.build();
						//@formatter:on
						chargesRawList.add(chargesRaw);
					}
				});
			}
		});

		return chargesRawList;
	}

	/**
	 * Создание сырых данных на основании начислений по фактам использования. \Хитрости:
	 * <ul>
	 * <li>если границы формирования типа аналитики {@link Analytic#getBoundaries()} не валидны, то такой тип аналитики
	 * не обрабатывается</li>
	 * <li>если для типа аналитики нет исходных данных, то такой тип аналитики не обрабатывается</li>
	 * <li>для данного типа сырых данных включать только инвойсы, дата окончания которых входит в период</li>
	 * </ul>
	 * 
	 * @param analytics
	 *            общий список типов аналитик, который нужно расчитать.
	 * @param billData
	 *
	 */
	private List<ChargesRaw> createRawByUsage(List<Analytic> analytics, BillData billData) {

		if (billData.getUsageInvoiceIds().isEmpty()) {
			return new ArrayList<>();
		}

		List<Analytic> analyticsForUsage = analytics.stream()
				.filter(analytic -> !analytic.getType().getIsRow() || analytic.getType().getChargesType().equals(USAGE))
				.collect(toList());

		List<ChargesQueryResult> usageInvoices = findUsageInvoices(billData);

		return analyticsForUsage.stream().filter(Analytic::isValidBoundaries).flatMap(analytic -> {
			return usageInvoices.stream()
					.filter(usageInvoice -> analytic.getBoundaries().contains(usageInvoice.getEndDate()))
					.map(usageInvoice -> {
				//@formatter:off
				return ChargesRawByUsage.builder()
						.analyticTypeId(analytic.getType().getId())
						.optionId(usageInvoice.getSubjectId())
						.taxRate(convertTax(usageInvoice.getTaxRate()))
						.startDate(usageInvoice.getStartDate())
						.endDate(usageInvoice.getEndDate())
						.sum(usageInvoice.getSum())
						.row(analytic.getType().getIsRow())
						.serviceId(usageInvoice.getServiceId())
						.providerId(usageInvoice.getProviderId())
						.withoutContract(usageInvoice.isWithoutContract())
					.build();
				//@formatter:on
					});
		}).collect(toList());
	}

	/**
	 * Создаёт сырые данные по подписке и инвойс плану. Определяя, должен ли этот инвойс план быть включён в расчёт
	 * сырых данных для аналитики. Правила включения инвойс плана в аналитику:
	 * <ul>
	 * <li>период, за который построен инвойс план полностью входит в период расчёта аналитики</li>
	 * <li>нижняя граница периода построения инвойс плана входит в период расчёта аналитики, а способ оплаты равняется
	 * {@linkplain PaymentCondition#PREPAYMENT}</li>
	 * <li>верхняя граница периода построения инвойс плана входит в период расчёта аналитики, а способ оплаты равняется
	 * {@linkplain PaymentCondition#POSTPAYMENT}</li>
	 * </ul>
	 */
	private ChargesRawByRecurrent createRawByRecurrent(Analytic analytic, Subscription subscription,
			InvoicePlan invoicePlan, PaymentCondition paymentCondition) {
		boolean containsStartDate = analytic.getBoundaries().contains(invoicePlan.plannedPeriod().startDate());
		boolean containsEndDate = analytic.getBoundaries().contains(invoicePlan.plannedPeriod().endDate());
		boolean planInsidePeriod = containsStartDate && containsEndDate;
		boolean planIncludeIntoPrepayment = containsStartDate && PREPAYMENT.equals(paymentCondition);
		boolean planIncludeIntoPostpayment = containsEndDate && POSTPAYMENT.equals(paymentCondition);

		if (planInsidePeriod || planIncludeIntoPrepayment || planIncludeIntoPostpayment) {
			AbstractContract<?> absContract = initializeAndUnproxy(
					((ContractSubjectCause) subscription.getSubjectCause()).getContractEntry().getContract());
			Contract contract;
			if (absContract instanceof ContractExtension) {
				contract = ((ContractExtension) absContract).getContract();
			} else {
				contract = (Contract) absContract;
			}
			ContractCategory category = contract.getType().getContractCategory();

			BigDecimal tax = category == ContractCategory.BILATERAL
					? ((Owner) initializeAndUnproxy(contract.getType().getProvider())).getTaxRate()
					: ((Owner) initializeAndUnproxy(contract.getBroker())).getTaxRate();
			//@formatter:off
			return ChargesRawByRecurrent.builder()
						.analyticTypeId(analytic.getType().getId())
						.productId(subscription.getSubject().getId())
						.subscriptionId(subscription.getId())
						.taxRate(convertTax(tax))
						.startDate(invoicePlan.summary().startDate())
						.endDate(invoicePlan.summary().endDate())
						.sum(invoicePlan.summary().totalCost().getAmount())
						.discountSum(invoicePlan.summary().deltaCost().abs().getAmount())
						.row(analytic.getType().getIsRow())
					.build();
			//@formatter:on
		} else {
			return null;
		}
	}

	private ChargesRawByRecurrent createInvalidOrEmptyRaw(Analytic analytic, Long subsId, Long productId,
			BigDecimal taxRate, AnalyticTypeError error) {
		//@formatter:off
		return ChargesRawByRecurrent.builder()
				.analyticTypeId(analytic.getType().getId())
				.productId(productId)
				.subscriptionId(subsId)
				.taxRate(convertTax(taxRate))
				.startDate(analytic.getStartDate())
				.endDate(analytic.getEndDate())
				.error(error)
				.sum(ZERO)
				.discountSum(ZERO)
				.row(analytic.getType().getIsRow())
			.build();
		//@formatter:on
	}

	private static final String FIND_SHORT_TERM_INVOICES = "ChargesService.findShortTermInvoices";

	/**
	 * Поиск всех инвойсов по единоразовым продуктам/услугам по идентификаторам инвойсов.
	 *
	 * @param billData
	 */
	//@formatter:off
	@NamedNativeQuery(name = FIND_SHORT_TERM_INVOICES, resultSetMapping = NONRECURRENT_QUERY_RESULT_MAPPER,
			query = "SELECT\n" +
					"  i.id               AS invoice_id,\n" +
					"  po.product_type_id AS subject_id,\n" +
					"  i.creation_date    AS start_date,\n" +
					"  i.closing_date     AS end_date,\n" +
					"  ie.amount          AS sum,\n" +
					"  (\n" +
					"    SELECT o.tax_rate\n" +
					"    FROM\n" +
					"      system.pricelist p, system.owner o\n" +
					"    WHERE\n" +
					"      p.id = po.pricelist_id\n" +
					"      AND p.owner_id = o.id\n" +
					"  )                  AS tax_rate\n" +
					"FROM\n" +
					"  system.invoice i,\n" +
					"  system.invoice_entry ie,\n" +
					"  system.product_offering po\n" +
					"WHERE\n" +
					"  i.id IN (:invoice_ids)\n" +
					"  AND i.id = ie.invoice_id\n" +
					"  AND ie.product_offering_id = po.id\n" +
					"  AND i.dtype = 'ShortTermInvoice'")
	//@formatter:on
	private List<ChargesQueryResult> findShortTermInvoices(BillData billData) {
		if (billData.getShortTermInvoiceIds() == null || billData.getShortTermInvoiceIds().isEmpty()) {
			return new ArrayList<>();
		}
		return em.createNamedQuery(FIND_SHORT_TERM_INVOICES, ChargesQueryResult.class)
				.setParameter("invoice_ids", billData.getShortTermInvoiceIds()).getResultList();
	}

	private static final String FIND_USAGE_INVOICES = "ChargesService.findUsageInvoices";

	/**
	 * Поиск инвойсов по фактам использованиям по идентификаторам инвойсов.
	 *
	 * @param billData
	 */
	//@formatter:off
	@NamedNativeQuery(name = FIND_USAGE_INVOICES, resultSetMapping = USAGE_QUERY_RESULT_MAPPER,
			query = "SELECT\n" + 
					"    i.id                                        AS invoice_id,\n" + 
					"    i.option_id                                 AS subject_id,\n" + 
					"    i.start_date                                AS start_date,\n" + 
					"    i.end_date                                  AS end_date,\n" + 
					"    i.price                                     AS sum,\n" + 
					"    (SELECT o.tax_rate\n" + 
					"     FROM\n" + 
					"         system.owner o,\n" + 
					"         system.contract con\n" + 
					"         JOIN system.contract_type ct ON con.contract_type_id = ct.id\n" + 
					"     WHERE CASE WHEN ct.contract_category = 'BILATERAL'\n" + 
					"         THEN o.id = ct.provider_id\n" + 
					"           ELSE o.id = con.broker_id END\n" + 
					"           AND CASE WHEN c.contract_id IS NULL\n" + 
					"         THEN c.id = con.id\n" + 
					"               ELSE c.contract_id = con.id END) AS tax_rate,\n" + 
					"    i.service_id                                AS service_id,\n" + 
					"    i.provider_id                               AS provider_id,\n" + 
					"    i.without_contract                          AS without_contract\n" + 
					"FROM\n" + 
					"    SYSTEM.invoice i\n" + 
					"    JOIN SYSTEM.commodity op ON i.option_id = op.id\n" + 
					"    JOIN SYSTEM.contract_entry ce ON op.option_subject_id = ce.id\n" + 
					"    JOIN SYSTEM.contract C ON ce.contract_id = C.id\n" + 
					"WHERE i.dtype = 'UsageInvoice'\n" + 
					"      AND i.id IN (:invoice_ids);")
	//@formatter:on
	private List<ChargesQueryResult> findUsageInvoices(BillData billData) {
		if (billData.getUsageInvoiceIds() == null || billData.getUsageInvoiceIds().isEmpty()) {
			return new ArrayList<>();
		}

		return em.createNamedQuery(FIND_USAGE_INVOICES, ChargesQueryResult.class)
				.setParameter("invoice_ids", billData.getUsageInvoiceIds()).getResultList();
	}

	/**
	 * Конвертирует значение процентов. Пример: 18 в 0.18
	 */
	private BigDecimal convertTax(BigDecimal taxRate) {
		return taxRate.divide(BigDecimal.valueOf(100L));
	}

	private static final long serialVersionUID = -7741970089285808747L;

}