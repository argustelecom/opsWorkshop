package ru.argustelecom.box.env.billing.bill.model;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.document.model.DocumentType;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.model.role.Supplier;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

/**
 * Спецификация счётов выставляемых клиентам на оплату предоставляемых услуг.
 */
//@formatter:off
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "bill_type")
@AssociationOverrides({
	@AssociationOverride(
		name = "templates",
		joinTable = @JoinTable(
			schema = "system",
			name = "bill_templates",
			joinColumns = @JoinColumn(name = "bill_type_id"),
			inverseJoinColumns = @JoinColumn(name = "bill_template_id")
		)
	)
})
//@formatter:on
public class BillType extends DocumentType {

	public final static Set<Class<? extends PartyRole>> VALID_PROVIDER_CLASSES;

	static {
		Set<Class<? extends PartyRole>> classes = newHashSet();
		classes.add(Owner.class);
		classes.add(Supplier.class);
		VALID_PROVIDER_CLASSES = unmodifiableSet(classes);
	}

	/**
	 * Тип периода, за будут формироваться счета данной спецификации.
	 */
	@Getter
	@Setter
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BillPeriodType periodType;

	/**
	 * Единица измерения типа периода (месяц, квартал, полугодие, год). Указывается только для календарного
	 * {@link #periodType}.
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private PeriodUnit periodUnit;

	/**
	 * Способ группировки подписок, для клиента.
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private GroupingMethod groupingMethod;

	/**
	 * Условие оплаты. В рамках одного счёта не могут участвовать подписки с разными условиями оплаты.
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private PaymentCondition paymentCondition;

	/**
	 * Тип клиента, если он не указан, то считаем, что срецификация счёта для всех типов клиентов.
	 */
	@Getter
	@Setter
	@ManyToOne(targetEntity = CustomerType.class, fetch = LAZY)
	@JoinColumn(name = "customer_type_id")
	private CustomerType customerType;

	/**
	 * Список всех аналитик, которые необходимо расчитать для счёта на основании данной спецификации.
	 */
	//@formatter:off
	@OneToMany(targetEntity = AbstractBillAnalyticType.class, fetch = LAZY)
	@JoinTable(
			schema = "system",
			name = "bill_type_analytics",
			joinColumns = @JoinColumn(name = "bill_type_id"),
			inverseJoinColumns = @JoinColumn(name = "analytic_id")
	)
	//@formatter:on
	private List<AbstractBillAnalyticType> analytics = new ArrayList<>();

	/**
	 * Самая главная сумма к оплате в счёте, должна быть выбрана из списка итоговых аналитик.
	 */
	@Getter
	@Setter
	@ManyToOne(targetEntity = SummaryBillAnalyticType.class, fetch = LAZY)
	@JoinColumn(name = "summary_analytic_id")
	private SummaryBillAnalyticType summaryToPay;

	/**
	 * Поставщики, для которых применим данный тип счета
	 */
	@Setter
	@OneToMany(fetch = LAZY)
	//@formatter:off
	@JoinTable(
			schema = "system",
			name = "bill_type_provider",
			joinColumns = @JoinColumn(name = "bill_type_id"),
			inverseJoinColumns = @JoinColumn(name = "party_role_id")
	)
	//@formatter:on
	private List<PartyRole> providers;

	protected BillType() {
	}

	protected BillType(Long id) {
		super(id);
	}

	public List<AbstractBillAnalyticType> getAnalytics() {
		return unmodifiableList(analytics);
	}

	public void replaceAnalyticsWith(List<AbstractBillAnalyticType> analytics) {
		this.analytics.clear();
		this.analytics.addAll(analytics);
	}

	public void addAnalytic(AbstractBillAnalyticType analytic) {
		if (!analytics.contains(analytic)) {
			analytics.add(analytic);
		}
	}

	public void removeAnalytic(AbstractBillAnalyticType analytic) {
		analytics.remove(analytic);
	}

	public List<BillAnalyticType> getBillAnalyticTypes() {
		return getAnalyticTypesByClass(BillAnalyticType.class);
	}

	public List<SummaryBillAnalyticType> getSummaryBillAnalyticTypes() {
		return getAnalyticTypesByClass(SummaryBillAnalyticType.class);
	}

	public List<PartyRole> getProviders() {
		return unmodifiableList(providers);
	}

	private <T extends AbstractBillAnalyticType> List<T> getAnalyticTypesByClass(Class<T> clazz) {
		return analytics.stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
	}

	public static class BillTypeQuery<T extends BillType> extends TypeQuery<T> {

		private EntityQuerySimpleFilter<T, BillPeriodType> periodType;
		private EntityQuerySimpleFilter<T, PeriodUnit> periodUnit;
		private EntityQuerySimpleFilter<T, GroupingMethod> groupingMethod;
		private EntityQuerySimpleFilter<T, PaymentCondition> paymentCondition;
		private EntityQueryEntityFilter<T, CustomerType> customerType;

		public BillTypeQuery(Class<T> entityClass) {
			super(entityClass);
			periodType = createFilter(BillType_.periodType);
			periodUnit = createFilter(BillType_.periodUnit);
			groupingMethod = createFilter(BillType_.groupingMethod);
			paymentCondition = createFilter(BillType_.paymentCondition);
			customerType = createEntityFilter(BillType_.customerType);
		}

		public EntityQuerySimpleFilter<T, BillPeriodType> periodType() {
			return periodType;
		}

		public EntityQuerySimpleFilter<T, PeriodUnit> periodUnit() {
			return periodUnit;
		}

		public EntityQuerySimpleFilter<T, GroupingMethod> groupingMethod() {
			return groupingMethod;
		}

		public EntityQuerySimpleFilter<T, PaymentCondition> paymentCondition() {
			return paymentCondition;
		}

		public EntityQueryEntityFilter<T, CustomerType> customerType() {
			return customerType;
		}
	}

	private static final long serialVersionUID = 7281902312930767306L;
}