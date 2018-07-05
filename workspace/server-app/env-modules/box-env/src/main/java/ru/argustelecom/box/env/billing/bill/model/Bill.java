package ru.argustelecom.box.env.billing.bill.model;

import static java.lang.String.format;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.BillReportContextService;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.document.model.Document;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.env.report.api.ReportContext;
import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.modelbase.SequenceDefinition;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryNumericFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.CDIHelper;

/**
 * Счёт выставляемый клиентам на оплату.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "bill", uniqueConstraints = @UniqueConstraint(name = "uc_bill", columnNames = {
		"grouping_method", "group_id", "start_date", "end_date", "payment_condition", "provider_id", "broker_id" }))
@SequenceDefinition(name = "system.gen_bill_id")
public class Bill extends Document<BillType> implements Printable {

	private static final long serialVersionUID = 4138899838222104060L;

	/**
	 * Поставщик. Может быть либо Owner, либо Supplier
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_id")
	private PartyRole provider;

	/**
	 * Агент. Может быть только Owner
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "broker_id")
	private Owner broker;

	/**
	 * Тип периода формирования квитанции.
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BillPeriodType periodType;

	/**
	 * Единица типа периода. Определяет даты формирования счёта для календарных типов.
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private PeriodUnit periodUnit;

	/**
	 * Дата начала формирования квитанции.
	 */
	@Getter
	@Setter
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	/**
	 * Дата окончания формирования квитанции.
	 */
	@Getter
	@Setter
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	/**
	 * Клиент, для которого формируется квитанция.
	 */
	@Getter
	@Setter
	@ManyToOne(targetEntity = Customer.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	/**
	 * Метод группировки подписок в квитанции.
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GroupingMethod groupingMethod;

	/**
	 * Идентификатор группирующего объекта (договора/лицевого счёта), какой конкретно можно определить исходя из
	 * {@link #groupingMethod}.
	 */
	@Getter
	@Setter
	@Column(nullable = false)
	private Long groupId;

	/**
	 * Условие оплаты.
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentCondition paymentCondition;

	/**
	 * Шаблон печатной формы для счёта.
	 */
	@Getter
	@Setter
	@ManyToOne(targetEntity = ReportModelTemplate.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "template_id", nullable = false)
	private ReportModelTemplate template;

	@Getter
	@Setter
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "bill_raw_data_id")
	private BillRawData billRawData;

	@Getter
	@Embedded
	private AggDataContainer aggDataContainer;

	/**
	 * Итоговая сумма по всем начислениям за период формирования квитанции c учётом налоговой ставки.
	 */
	@Getter
	@Setter
	@Embedded
	@AttributeOverride(name = "amount", column = @Column(name = "amount_with_tax"))
	private Money amountWithTax;

	/**
	 * Итоговая сумма по всем начислениям за период формирования квитанции с вычетом налоговой ставки.
	 */
	@Getter
	@Setter
	@Embedded
	@AttributeOverride(name = "amount", column = @Column(name = "amount_without_tax"))
	private Money amountWithoutTax;

	/**
	 * Итоговая сумма налоговой ставки, по всем позициям в счёте.
	 */
	@Getter
	@Setter
	@Embedded
	@AttributeOverride(name = "amount", column = @Column(name = "tax_amount"))
	private Money taxAmount;

	/**
	 * Итоговая сумма скидки, по всем позициям в счёте.
	 */
	@Getter
	@Setter
	@Embedded
	@AttributeOverride(name = "amount", column = @Column(name = "discount_amount"))
	private Money discountAmount;

	/**
	 * Результирующая сумма квитанции, которая и будет представлена к оплате для клиента. Определяется на основании
	 * {@link BillType#summaryToPay}.
	 */
	@Getter
	@Setter
	@Embedded
	@AttributeOverride(name = "amount", column = @Column(name = "total_amount"))
	private Money totalAmount;

	/**
	 * Дата последнего изменения счета
	 */
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;

	@Transient
	private BillPeriod period;

	@Transient
	private Identifiable groupingObject;

	protected Bill() {
	}

	protected Bill(Long id) {
		super(id);
	}

	@Override
	public void fillReportContext(ReportContext reportContext) {
		// FIXME пока так, в будущем планируется вынести метод из сущности
		CDIHelper.lookupCDIBean(BillReportContextService.class).fillReportContext(this, reportContext);
	}

	@Override
	public ReportData createReportData() {
		// FIXME пока так, в будущем планируется вынести метод из сущности
		return CDIHelper.lookupCDIBean(BillReportContextService.class).createReportData(this);
	}

	/**
	 * Спецификация счёта.
	 */
	@Override
	@Access(AccessType.PROPERTY)
	@ManyToOne(targetEntity = BillType.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "bill_type_id")
	public BillType getType() {
		return super.getType();
	}

	public BillPeriod getPeriod() {
		if (period == null) {
			switch (periodType) {
			case CALENDARIAN:
				period = BillPeriod.of(periodUnit, toLocalDateTime(startDate));
				break;
			case CUSTOM:
				period = BillPeriod.of(toLocalDateTime(startDate), toLocalDateTime(endDate));
				break;
			default:
				throw new SystemException(format("Unsupported period type: '%s'", periodType));
			}
		}
		return period;
	}

	public Identifiable getGroupingObject() {
		if (groupingObject == null) {
			groupingObject = getEntityManager().find(groupingMethod.getEntityClass(), groupId);
		}
		return groupingObject;
	}

	private static final String MISS_SUMMARY_TO_PAY_ANALYTIC_TYPE_ERR_TEMPLATE = "Для счёта: '%s' не расчитана аналитика: '%s', которая используется как сумма к оплате";

	/**
	 * Пересчитывает итоги счёта: {@link #amountWithoutTax}, {@link #amountWithTax}, {@link #taxAmount},
	 * {@link #totalAmount} на осовании сырых данных.
	 */
	public void recalculateAmounts() {
		recalculateAmountsByRow();
		Summary summaryToPay = findSummaryForTotalToPayAnalyticType();
		setTotalAmount(summaryToPay != null ? new Money(summaryToPay.getSumToPay()) : getAmountWithTax());
	}

	public void changeData(BillRawData billRawData, AggDataHolder aggDataHolder) {
		this.billRawData = billRawData;
		this.aggDataContainer = AggDataContainer.of(aggDataHolder);
		recalculateAmounts();
	}

	/**
	 * Ищет расчёты для итоговой аналитики, которая была выбрана как {@linkplain BillType#summaryToPay сумма к оплате}.
	 * Если она была не выбрана, то вернётся null т.к. значение должно равняться сумме по начислениям, являющимся
	 * строками. Если выбранный в спецификации счёта тип аналитики небыл расчитан, то будет брошено исключение.
	 */
	private Summary findSummaryForTotalToPayAnalyticType() {
		SummaryBillAnalyticType totalToPayAnalyticType = getType().getSummaryToPay();
		if (totalToPayAnalyticType != null) {
			return getAggDataContainer().getDataHolder().getSummaries().stream()
					.filter(summary -> summary.getAnalyticTypeId().equals(totalToPayAnalyticType.getId())).findFirst()
					.orElseThrow(() -> new SystemException(format(MISS_SUMMARY_TO_PAY_ANALYTIC_TYPE_ERR_TEMPLATE,
							getDocumentNumber(), totalToPayAnalyticType.getName())));
		}
		return null;
	}

	/**
	 * Пересчитывает итоговые суммы: {@link #amountWithoutTax}, {@link #amountWithTax}, {@link #taxAmount}. На основании
	 * агрегированных сырых данных являющихся {@linkplain Bill#getRowChargesAggList() строками}.
	 */
	private void recalculateAmountsByRow() {
		BigDecimal amountWithTax = BigDecimal.ZERO;
		BigDecimal amountWithoutTax = BigDecimal.ZERO;
		BigDecimal taxAmount = BigDecimal.ZERO;
		BigDecimal discountAmount = BigDecimal.ZERO;
		for (ChargesAgg chargesAgg : getRowChargesAggList()) {
			amountWithTax = amountWithTax.add(chargesAgg.getSum());
			amountWithoutTax = amountWithoutTax.add(chargesAgg.getSumWithoutTax());
			taxAmount = taxAmount.add(chargesAgg.getTax());
			discountAmount = discountAmount.add(chargesAgg.getDiscountSum());
		}

		setAmountWithTax(new Money(amountWithTax));
		setTaxAmount(new Money(taxAmount));
		setAmountWithoutTax(new Money(amountWithoutTax));
		setDiscountAmount(new Money(discountAmount));
	}

	private List<ChargesAgg> getRowChargesAggList() {
		return getAggDataContainer().getDataHolder().getChargesAggList().stream().filter(ChargesAgg::isRow)
				.collect(Collectors.toList());
	}

	public static class BillQuery<I extends Bill> extends DocumentQuery<BillType, I> {

		private EntityQuerySimpleFilter<I, BillPeriodType> periodType;
		private EntityQuerySimpleFilter<I, PeriodUnit> periodUnit;
		private EntityQueryDateFilter<I> startDate;
		private EntityQueryDateFilter<I> endDate;
		private EntityQueryEntityFilter<I, Customer> customer;
		private EntityQuerySimpleFilter<I, PaymentCondition> paymentCondition;
		private EntityQuerySimpleFilter<I, GroupingMethod> groupingMethod;
		private EntityQueryNumericFilter<I, Long> groupId;
		private Join<Bill, BillType> billTypeJoin;
		private EntityQueryEntityFilter<I, PartyRole> provider;
		private EntityQueryEntityFilter<I, Owner> broker;

		public BillQuery(Class<I> entityClass) {
			super(entityClass);
			periodType = createFilter(Bill_.periodType);
			periodUnit = createFilter(Bill_.periodUnit);
			startDate = createDateFilter(Bill_.startDate);
			endDate = createDateFilter(Bill_.endDate);
			customer = createEntityFilter(Bill_.customer);
			paymentCondition = createFilter(Bill_.paymentCondition);
			groupingMethod = createFilter(Bill_.groupingMethod);
			groupId = createNumericFilter(Bill_.groupId);
			provider = createEntityFilter(Bill_.provider);
			broker = createEntityFilter(Bill_.broker);
		}

		@Override
		protected EntityQueryEntityFilter<I, BillType> createTypeFilter() {
			return createEntityFilter(Bill_.type);
		}

		public EntityQuerySimpleFilter<I, PeriodUnit> periodUnit() {
			return periodUnit;
		}

		public EntityQuerySimpleFilter<I, BillPeriodType> periodType() {
			return periodType;
		}

		public EntityQueryDateFilter<I> startDate() {
			return startDate;
		}

		public EntityQueryDateFilter<I> endDate() {
			return endDate;
		}

		public EntityQueryEntityFilter<I, Customer> customer() {
			return customer;
		}

		public EntityQueryEntityFilter<I, PartyRole> provider() {
			return provider;
		}

		public EntityQueryEntityFilter<I, Owner> broker() {
			return broker;
		}

		public EntityQuerySimpleFilter<I, PaymentCondition> paymentCondition() {
			return paymentCondition;
		}

		public EntityQuerySimpleFilter<I, GroupingMethod> groupingMethod() {
			return groupingMethod;
		}

		public EntityQueryNumericFilter<I, Long> groupId() {
			return groupId;
		}

		public Predicate byCustomerType(CustomerType customerType) {
			return criteriaBuilder().equal(billTypeJoin().get(BillType_.customerType),
					createParam(BillType_.customerType, customerType));
		}

		private Join<Bill, BillType> billTypeJoin() {
			if (billTypeJoin == null) {
				billTypeJoin = root().join(Bill_.type.getName(), JoinType.INNER);
			}
			return billTypeJoin;
		}
	}
}