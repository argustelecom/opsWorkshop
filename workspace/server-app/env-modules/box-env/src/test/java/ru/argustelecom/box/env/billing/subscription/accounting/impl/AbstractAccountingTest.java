package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static ru.argustelecom.box.env.billing.period.PeriodBuilderService.chargingOf;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.chargingOf;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToDate;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToLdt;
import static ru.argustelecom.box.env.stl.period.PeriodType.CALENDARIAN;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPeriodModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPriceModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanPeriod;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanTimeline;
import ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlanImpl.InvoicePlannedPeriod;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;
import ru.argustelecom.box.env.stl.period.Period;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;

public abstract class AbstractAccountingTest {

	public static final PeriodType PERIOD_TYPE = PeriodType.CALENDARIAN;
	public static final PeriodDuration ACCOUNTING_DURATION = PeriodDuration.ofMonths(1);
	public static final PeriodDuration CHARGING_DURATION = PeriodDuration.ofDays(3);
	public static final Date START_OF_INTEREST = strToDate("2017-10-15 12:00:00.000");
	public static final Money COST = new Money("300");

	/**
	 * 2018-01-08 00:00:00.000 --- 2018-01-14 23:59:59.999, 7 БЕ * 10 УЕ = 70 УЕ
	 */
	public static ChargingPeriod defaultCp = chargingOf(CALENDARIAN, "2018-01-01 00:00:00.000",
			"2018-01-08 00:00:00.000", "310", PeriodDuration.of(1, PeriodUnit.MONTH),
			PeriodDuration.of(1, PeriodUnit.WEEK));

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	protected Subscription subscription;

	@Mock
	protected RecurrentTerms provisionTerms;

	@Mock
	protected PersonalAccount personalAccount;

	protected EnumMap<CPID, ChargingPeriod> chargingPeriods;

	protected AtomicLong invoiceIdCounter = new AtomicLong(100L);

	@Before
	@SuppressWarnings("unchecked")
	public void setup() {
		when(provisionTerms.getPeriodType()).thenReturn(PERIOD_TYPE);
		when(provisionTerms.getChargingDuration()).thenReturn(CHARGING_DURATION);
		when(provisionTerms.getRoundingPolicy()).thenReturn(RoundingPolicy.UP);
		when(provisionTerms.isReserveFunds()).thenReturn(false);

		when(subscription.getValidFrom()).thenReturn(START_OF_INTEREST);
		when(subscription.getValidTo()).thenReturn(null);
		when(subscription.getCost()).thenReturn(COST);
		when(subscription.getProvisionTerms()).thenReturn(provisionTerms);
		when(subscription.getAccountingDuration()).thenReturn(ACCOUNTING_DURATION);
		when(subscription.inState((SubscriptionState) Mockito.any())).thenCallRealMethod();
		when(subscription.inState(Mockito.anyCollection())).thenCallRealMethod();
		when(subscription.getState()).thenReturn(SubscriptionState.ACTIVE);
		when(subscription.getPersonalAccount()).thenReturn(personalAccount);

		rebuildChargingPeriods();
	}

	public void rebuildChargingPeriods() {
		chargingPeriods = new EnumMap<>(CPID.class);
		ChargingPeriod cp = chargingOf(subscription);
		for (CPID cpid : CPID.values()) {
			chargingPeriods.put(cpid, cp);
			cp = cp.next();
		}
	}

	@After
	public void cleanup() {
		subscription = null;
		provisionTerms = null;
		chargingPeriods = null;
	}

	public ChargingPeriod cpOf(CPID id) {
		return chargingPeriods.get(id);
	}

	// @formatter:off
	
	public InvoicePlan planOf(CPID cpid, boolean isPresent) {
		return planOf(cpid, null, null, isPresent);
	}
	
	public InvoicePlan planOf(CPID cpid, LocalDateTime plannedStart, LocalDateTime plannedEnd, boolean isPresent) {
		ChargingPeriod cp = cpOf(cpid);
		
		InvoicePlanBuilder builder = new InvoicePlanBuilder()
			.setChargingPeriod(cp)
			.setRoundingPolicy(RoundingPolicy.UP)
			.setPlannedStart(plannedStart != null ? plannedStart : cp.startDateTime())
			.setPlannedEnd(plannedEnd != null ? plannedEnd : cp.endDateTime())
			.setTimeline(isPresent ? InvoicePlanTimeline.PRESENT : InvoicePlanTimeline.PAST);
		
		builder.validateInput();
		
		Range<LocalDateTime> boundaries = builder.roundBoundaries();
		List<InvoicePlanPeriodImpl> details = singletonList(builder.createInvoicePeriod(boundaries, null));
		InvoicePlanPeriodImpl summary = builder.createSummary(boundaries, details);
		
		return builder.createPlan(summary, details);
	}

	public List<InvoicePlan> planOf(CPID from, CPID to, boolean includePresent) {
		return Stream.of(CPID.values())
			.filter(id -> id.compareTo(from) >= 0 && id.compareTo(to) <= 0)
			.map(id -> planOf(id, includePresent && id == to))
			.collect(Collectors.toList());
	}

	public static InvoicePlanPriceModifier priceModifier(String validFrom, String validTo) {
		return new PriceModifierStub(
			strToDate(validFrom), 
			strToDate(validTo), 
			"Скидка 30%", 
			new BigDecimal("0.7")
		);
	}
	
	public static InvoicePlanPriceModifier priceModifier(String validFrom, String validTo, String priceFactor) {
		BigDecimal factor = new BigDecimal(priceFactor);
		String modifierName = BigDecimal.ONE.subtract(factor).multiply(new BigDecimal(100)).toString();
		
		return new PriceModifierStub(
			strToDate(validFrom), 
			strToDate(validTo), 
			"Скидка " + modifierName + "%", 
			factor
		);
	}
	
	public static InvoicePlanPriceModifier priceModifier(String validFrom, String validTo, String objectName, String priceFactor) {
		return new PriceModifierStub(
			strToDate(validFrom), 
			strToDate(validTo), 
			objectName, 
			new BigDecimal(priceFactor)
		);
	}

	public static InvoicePlanPeriodModifier complexModifier(String validFrom, String validTo, String objectName, String priceFactor) {
		return new ComplexModifierStub(
			strToDate(validFrom), 
			strToDate(validTo), 
			objectName, 
			new BigDecimal(priceFactor)
		);
	}
	
	public static InvoicePlanPeriodModifier periodModifier(String validFrom, String validTo, String objectName) {
		return new PeriodModifierStub(
			strToDate(validFrom), 
			strToDate(validTo), 
			objectName
		);
	}
	
	public static InvoicePlan previousPlan(ChargingPeriod cp) {
		return previousPlan(cp, cp.startDateTime(), cp.endDateTime());
	}

	public static InvoicePlan previousPlan(ChargingPeriod cp, String billingStart, String billingEnd) {
		return previousPlan(cp, strToLdt(billingStart), strToLdt(billingEnd));
	}

	public static InvoicePlan previousPlan(ChargingPeriod cp, LocalDateTime billingStart, LocalDateTime billingEnd) {
		InvoicePlanPeriodImpl summary = mock(InvoicePlanPeriodImpl.class,
				withSettings().defaultAnswer(CALLS_REAL_METHODS));

		when(summary.boundaries()).thenReturn(Range.closed(billingStart, billingEnd));
		when(summary.cost()).thenThrow(new UnsupportedOperationException());
		when(summary.baseUnitCost()).thenThrow(new UnsupportedOperationException());
		when(summary.baseUnitCount()).thenThrow(new UnsupportedOperationException());
		when(summary.totalCost()).thenThrow(new UnsupportedOperationException());
		when(summary.deltaCost()).thenThrow(new UnsupportedOperationException());

		InvoicePlanImpl previousPlan = mock(InvoicePlanImpl.class);
		when(previousPlan.chargingPeriod()).thenReturn(cp);
		when(previousPlan.roundingPolicy()).thenReturn(RoundingPolicy.UP);
		when(previousPlan.plannedPeriod()).thenReturn(summary);
		when(previousPlan.summary()).thenReturn(summary);
		when(previousPlan.details()).thenReturn(singletonList(summary));

		return previousPlan;
	}

	public InvoicePlannerConfig config(String lowerBound, String upperBound, String renewalDate) {
		return config(null, lowerBound, upperBound, renewalDate, false);
	}

	public InvoicePlannerConfig config(InvoicePlan lastPlan, String lowerBound, String upperBound, String renewalDate) {
		return config(lastPlan, lowerBound, upperBound, renewalDate, false);
	}
	
	public InvoicePlannerConfig config(InvoicePlan lastPlan, String lowerBound, String upperBound, String renewalDate, 
			boolean allowPrimaryActivation) {
		
		InvoicePlannerConfig config = new InvoicePlannerConfig()
			.setSubscription(subscription)
			.setLastPlan(lastPlan)
			.setBoundaries(
				lowerBound != null ? strToLdt(lowerBound) : null, 
				upperBound != null ? strToLdt(upperBound) : null
			)
			.setRenewalDate(
				renewalDate != null ? strToLdt(renewalDate) : null
			)
			.setAllowPrimaryActivation(allowPrimaryActivation);
		
		config.prepare();
		return config;
	}
	
	public LongTermInvoice mockInvoice() {
		return mockInvoice((InvoicePlan) null, null);
	}

	public LongTermInvoice mockInvoice(CPID previousCpid) {
		return mockInvoice(previousCpid, null);
	}

	public LongTermInvoice mockInvoice(InvoicePlan previousPlan) {
		return mockInvoice(previousPlan, null);
	}

	public LongTermInvoice mockInvoice(CPID previousCpid, Privilege privilege, Discount... discounts) {
		return mockInvoice(planOf(previousCpid, false), privilege, discounts);
	}

	@SuppressWarnings("unchecked")
	public LongTermInvoice mockInvoice(InvoicePlan previousPlan, Privilege privilege, Discount... discounts) {
		LongTermInvoice invoice = Mockito.mock(LongTermInvoice.class);

		InvoicePlannerConfig config = new InvoicePlannerConfig();
		config.setSubscription(subscription);
		config.setLastPlan(previousPlan);
		config.addPeriodModifier(privilege);

		SubscriptionState savedSubscriptionState = subscription.getState();
		if (previousPlan == null) {
			when(subscription.getState()).thenReturn(SubscriptionState.FORMALIZATION);
			config.setAllowPrimaryActivation(true);
		}

		for (Discount discount : discounts) {
			config.addPriceModifier(discount);
		}

		InvoicePlan plan = new InvoicePlanner(config).createFuturePlans().get(0);
		Long invoiceId = invoiceIdCounter.getAndIncrement();

		when(invoice.getId()).thenReturn(invoiceId);
		when(invoice.getStartDate()).thenReturn(plan.plannedPeriod().startDate());
		when(invoice.getEndDate()).thenReturn(plan.plannedPeriod().endDate());
		when(invoice.getSubscription()).thenReturn(subscription);
		when(invoice.getRoundingPolicy()).thenReturn(plan.roundingPolicy());
		when(invoice.getState()).thenReturn(InvoiceState.CREATED);
		when(invoice.getTimeline()).thenReturn(InvoicePlanTimeline.FUTURE);
		when(invoice.getPlan()).thenReturn(plan);
		when(invoice.getPrice()).thenReturn(plan.summary().cost());
		when(invoice.getDiscountValue()).thenReturn(plan.summary().deltaCost().abs());
		when(invoice.getTotalPrice()).thenReturn(plan.summary().totalCost());
		when(invoice.getPrivilege()).thenReturn(privilege);
		when(invoice.getDiscounts()).thenReturn(asList(discounts));
		
		when(invoice.inState((InvoiceState) Mockito.any())).thenCallRealMethod();
		when(invoice.inState(Mockito.anyCollection())).thenCallRealMethod();
		when(invoice.isReserveSupported()).thenCallRealMethod();
		
		when(invoice.detachReserve()).thenThrow(UnsupportedOperationException.class);
		doThrow(UnsupportedOperationException.class).when(invoice).applyPlan(Mockito.any());
		doThrow(UnsupportedOperationException.class).when(invoice).attachReserve(Mockito.any());
		doThrow(UnsupportedOperationException.class).when(invoice).unsafeAddDiscount(Mockito.any());
		doThrow(UnsupportedOperationException.class).when(invoice).unsafeRemoveDiscount(Mockito.any());
		doThrow(UnsupportedOperationException.class).when(invoice).unsafeSetPrivilege(Mockito.any());
		
		if (plan.summary().modifier() != null) {
			InvoicePlanModifier modifier = plan.summary().modifier();
			when(invoice.resolveModifier(modifier.getId())).thenReturn(modifier);
		}

		for (InvoicePlanPeriod detail : plan) {
			if (detail.modifier() != null) {
				when(invoice.resolveModifier(detail.modifier().getId())).thenReturn(detail.modifier());
			}
		}

		if (previousPlan == null) {
			when(subscription.getState()).thenReturn(savedSubscriptionState);
		}

		return invoice;
	}
	
	// @formatter:on

	public abstract static class AbstractModifierStub implements InvoicePlanModifier {

		private Long id = 100L;
		private Date validFrom;
		private Date validTo;
		private String objectName;

		public AbstractModifierStub(Date validFrom, Date validTo, String objectName) {
			this.validFrom = validFrom;
			this.validTo = validTo;
			this.objectName = objectName;
		}

		@Override
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		@Override
		public String getObjectName() {
			return objectName;
		}

		@Override
		public Date getValidFrom() {
			return validFrom;
		}

		@Override
		public Date getValidTo() {
			return validTo;
		}
	}

	public static class PeriodModifierStub extends AbstractModifierStub implements InvoicePlanPeriodModifier {
		public PeriodModifierStub(Date validFrom, Date validTo, String objectName) {
			super(validFrom, validTo, objectName);
		}
	}

	public static class PriceModifierStub extends AbstractModifierStub implements InvoicePlanPriceModifier {
		private BigDecimal priceFactor;

		public PriceModifierStub(Date validFrom, Date validTo, String objectName, BigDecimal priceFactor) {
			super(validFrom, validTo, objectName);
			this.priceFactor = priceFactor;
		}

		@Override
		public BigDecimal getPriceFactor() {
			return priceFactor;
		}
	}

	public static class ComplexModifierStub extends PriceModifierStub implements InvoicePlanPeriodModifier {
		public ComplexModifierStub(Date validFrom, Date validTo, String objectName, BigDecimal priceFactor) {
			super(validFrom, validTo, objectName, priceFactor);
		}
	}

	public static class SubscriptionSettings {
		private SubscriptionState state = SubscriptionState.ACTIVE;
		private Date validFrom = START_OF_INTEREST;
		private Date validTo;

		public SubscriptionSettings inState(SubscriptionState state) {
			this.state = state;
			return this;
		}

		public SubscriptionSettings validFrom(String validFrom) {
			this.validFrom = strToDate(validFrom);
			return this;
		}

		public SubscriptionSettings validTo(String validTo) {
			this.validTo = strToDate(validTo);
			return this;
		}

		public void apply(Subscription subscriptionMock) {
			when(subscriptionMock.getState()).thenReturn(state);
			when(subscriptionMock.getValidFrom()).thenReturn(validFrom);
			when(subscriptionMock.getValidTo()).thenReturn(validTo);
		}

		public static SubscriptionSettings subscription() {
			return new SubscriptionSettings();
		}
	}

	public static class InvoicePlanExpectation {

		private CPID chargingPeriod;
		private RoundingPolicy roundingPolicy;
		private Period plannedPeriod;
		private InvoicePlanPeriodExpectation summary;
		private List<InvoicePlanPeriodExpectation> details = new ArrayList<>();

		protected InvoicePlanExpectation(CPID cp, RoundingPolicy rp, Range<LocalDateTime> boundaries) {
			this.chargingPeriod = cp;
			this.roundingPolicy = rp;
			this.plannedPeriod = new InvoicePlannedPeriod(boundaries);
		}

		public InvoicePlanExpectation withSummary(InvoicePlanPeriodExpectation summary) {
			this.summary = summary;
			return this;
		}

		public InvoicePlanExpectation withDetails(InvoicePlanPeriodExpectation... details) {
			for (InvoicePlanPeriodExpectation detail : details) {
				this.details.add(detail);
			}
			return this;
		}

		public void test(InvoicePlan plan) {
			test(plan, null);
		}

		public void test(InvoicePlan plan, Function<CPID, ChargingPeriod> getCpFunc) {
			// @formatter:off
			if (getCpFunc != null) {
				assertThat(
					"Период списания соответствует ожидаемому",
					plan.chargingPeriod(), equalTo(getCpFunc.apply(chargingPeriod))
				);
			}

			assertThat(
				"Политика списания соответствет ожидаемой",
				plan.roundingPolicy(), equalTo(roundingPolicy)
			);
			assertThat(
				"Планируемая дата начала соответствует ожидаемой",	
				plan.plannedPeriod().startDateTime(), equalTo(plannedPeriod.startDateTime())
			);
			assertThat(
				"Планируемая дата окончания соответствует ожидаемой",	
				plan.plannedPeriod().endDateTime(), equalTo(plannedPeriod.endDateTime())
			);

			summary.test(plan.summary());

			assertThat(plan.details().size(), equalTo(details.size()));
			for (int i = 0; i < details.size(); i++) {
				details.get(i).test(plan.details().get(i));
			}
			// @formatter:on
		}

		public static InvoicePlanExpectation plan(String start, String end, CPID cp) {
			return new InvoicePlanExpectation(cp, RoundingPolicy.UP, Range.closed(strToLdt(start), strToLdt(end)));
		}

		public static InvoicePlanExpectation plan(String start, String end, CPID cp, RoundingPolicy rp) {
			return new InvoicePlanExpectation(cp, rp, Range.closed(strToLdt(start), strToLdt(end)));
		}
	}

	public static class InvoicePlanPeriodExpectation {

		private LocalDateTime lowerBound;
		private LocalDateTime upperBound;
		private long baseUnitCount;
		private Money cost;
		private Money totalCost;
		private Money deltaCost;

		protected InvoicePlanPeriodExpectation(LocalDateTime lowerBound, LocalDateTime upperBound, long baseUnitCount,
				Money cost, Money totalCost, Money deltaCost) {
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
			this.baseUnitCount = baseUnitCount;
			this.cost = cost;
			this.totalCost = totalCost;
			this.deltaCost = deltaCost;
		}

		public void test(InvoicePlanPeriod period) {
			// @formatter:off
			assertThat(
				"Начало InvoicePlanPeriod должно соответствовать ожидаемому",	
				period.startDateTime(), equalTo(lowerBound)
			);
			assertThat(
				"Окончание InvoicePlanPeriod должно соответствовать ожидаемому",	
				period.endDateTime(), equalTo(upperBound)
			);
			assertThat(
				"Количество единиц тарификации InvoicePlanPeriod должно соответствовать ожидаемому",	
				period.baseUnitCount(), equalTo(baseUnitCount)
			);
			assertThat(
				"Базовая стоимость InvoicePlanPeriod должна соответствовать ожидаемой",	
				period.cost().getRoundAmount(), equalTo(cost.getRoundAmount())
			);
			assertThat(
				"Итоговая стоимость InvoicePlanPeriod должна соответствовать ожидаемой",	
				period.totalCost().getRoundAmount(), equalTo(totalCost.getRoundAmount())
			);
			assertThat(
				"Разница базовой и итоговой стоимости InvoicePlanPeriod должна соответствовать ожидаемой",	
				period.deltaCost().getRoundAmount(), equalTo(deltaCost.getRoundAmount())
			);
			// @formatter:on
		}

		public static InvoicePlanPeriodExpectation period(String lowerBound, String upperBound, long baseUnitCount,
				String cost, String totalCost, String deltaCost) {

			return new InvoicePlanPeriodExpectation(strToLdt(lowerBound), strToLdt(upperBound), baseUnitCount,
					new Money(cost), new Money(totalCost), new Money(deltaCost));

		}

	}

	public void assertThatInvoicePlanEquals(InvoicePlan actual, InvoicePlan expected) {
		assertThat(actual.chargingPeriod(), equalTo(expected.chargingPeriod()));
		assertThat(actual.roundingPolicy(), equalTo(expected.roundingPolicy()));
		assertThat(actual.plannedPeriod().startDateTime(), equalTo(expected.plannedPeriod().startDateTime()));
		assertThat(actual.plannedPeriod().endDateTime(), equalTo(expected.plannedPeriod().endDateTime()));

		assertThatInvoicePlanPeriodEquals(actual.summary(), expected.summary());

		assertThat(actual.details().size(), equalTo(expected.details().size()));
		for (int i = 0; i < actual.details().size(); i++) {
			assertThatInvoicePlanPeriodEquals(actual.details().get(i), expected.details().get(i));
		}
	}

	public void assertThatInvoicePlanPeriodEquals(InvoicePlanPeriod actual, InvoicePlanPeriod expected) {
		assertThat(actual.startDateTime(), equalTo(expected.startDateTime()));
		assertThat(actual.endDateTime(), equalTo(expected.endDateTime()));
		assertThat(actual.baseUnitCount(), equalTo(expected.baseUnitCount()));
		assertThat(actual.baseUnitCost(), equalTo(expected.baseUnitCost()));
		assertThat(actual.cost().getAmount(), equalTo(expected.cost().getAmount()));
		assertThat(actual.totalCost().getAmount(), equalTo(expected.totalCost().getAmount()));
		assertThat(actual.deltaCost().getAmount(), equalTo(expected.deltaCost().getAmount()));
	}

	/**
	 * Таблица ближайших периодов списания для некоторой тестовой подписки
	 * 
	 * <p>
	 * 
	 * periodType = CALENDARIAN<br>
	 * validFrom = 2017-10-15 12:00:00.000<br>
	 * Ta = 1 Month<br>
	 * Tc = 3 Day<br>
	 * $$ = 300 y.e.
	 * 
	 * <p>
	 * 
	 * <code>
	 * Tc01 {[2017-10-13 00:00:00.000 - 2017-10-15 23:59:59.999] 3 DAY, 29.03 y.e.}<br>
	 * Tc02 {[2017-10-16 00:00:00.000 - 2017-10-18 23:59:59.999] 3 DAY, 29.03 y.e.}<br>
	 * Tc03 {[2017-10-19 00:00:00.000 - 2017-10-21 23:59:59.999] 3 DAY, 29.03 y.e.}<br>
	 * Tc04 {[2017-10-22 00:00:00.000 - 2017-10-24 23:59:59.999] 3 DAY, 29.03 y.e.}<br>
	 * Tc05 {[2017-10-25 00:00:00.000 - 2017-10-27 23:59:59.999] 3 DAY, 29.03 y.e.}<br>
	 * Tc06 {[2017-10-28 00:00:00.000 - 2017-10-31 23:59:59.999] 4 DAY, 38.71 y.e.}<br>
	 * Tc07 {[2017-11-01 00:00:00.000 - 2017-11-03 23:59:59.999] 3 DAY, 30.00 y.e.}<br>
	 * Tc08 {[2017-11-04 00:00:00.000 - 2017-11-06 23:59:59.999] 3 DAY, 30.00 y.e.}<br>
	 * Tc09 {[2017-11-07 00:00:00.000 - 2017-11-09 23:59:59.999] 3 DAY, 30.00 y.e.}<br>
	 * Tc10 {[2017-11-10 00:00:00.000 - 2017-11-12 23:59:59.999] 3 DAY, 30.00 y.e.}<br>
	 * Tc11 {[2017-11-13 00:00:00.000 - 2017-11-15 23:59:59.999] 3 DAY, 30.00 y.e.}<br>
	 * Tc12 {[2017-11-16 00:00:00.000 - 2017-11-18 23:59:59.999] 3 DAY, 30.00 y.e.}<br>
	 * Tc13 {[2017-11-19 00:00:00.000 - 2017-11-21 23:59:59.999] 3 DAY, 30.00 y.e.}<br>
	 * Tc14 {[2017-11-22 00:00:00.000 - 2017-11-24 23:59:59.999] 3 DAY, 30.00 y.e.}<br>
	 * Tc15 {[2017-11-25 00:00:00.000 - 2017-11-27 23:59:59.999] 3 DAY, 30.00 y.e.}<br>
	 * Tc16 {[2017-11-28 00:00:00.000 - 2017-11-30 23:59:59.999] 3 DAY, 30.00 y.e.}<br>
	 * </code>
	 */
	public enum CPID {
		/**
		 * Tc01 {[2017-10-13 00:00:00.000 - 2017-10-15 23:59:59.999] 3 DAY, 29.03 y.e.}
		 */
		Tc01,

		/**
		 * Tc02 {[2017-10-16 00:00:00.000 - 2017-10-18 23:59:59.999] 3 DAY, 29.03 y.e.}
		 */
		Tc02,

		/**
		 * Tc03 {[2017-10-19 00:00:00.000 - 2017-10-21 23:59:59.999] 3 DAY, 29.03 y.e.}
		 */
		Tc03,

		/**
		 * Tc04 {[2017-10-22 00:00:00.000 - 2017-10-24 23:59:59.999] 3 DAY, 29.03 y.e.}
		 */
		Tc04,

		/**
		 * Tc05 {[2017-10-25 00:00:00.000 - 2017-10-27 23:59:59.999] 3 DAY, 29.03 y.e.}
		 */
		Tc05,

		/**
		 * Tc06 {[2017-10-28 00:00:00.000 - 2017-10-31 23:59:59.999] 4 DAY, 38.71 y.e.}
		 */
		Tc06,

		/**
		 * Tc07 {[2017-11-01 00:00:00.000 - 2017-11-03 23:59:59.999] 3 DAY, 30.00 y.e.}
		 */
		Tc07,

		/**
		 * Tc08 {[2017-11-04 00:00:00.000 - 2017-11-06 23:59:59.999] 3 DAY, 30.00 y.e.}
		 */
		Tc08,

		/**
		 * Tc09 {[2017-11-07 00:00:00.000 - 2017-11-09 23:59:59.999] 3 DAY, 30.00 y.e.}
		 */
		Tc09,

		/**
		 * Tc10 {[2017-11-10 00:00:00.000 - 2017-11-12 23:59:59.999] 3 DAY, 30.00 y.e.}
		 */
		Tc10,

		/**
		 * Tc11 {[2017-11-13 00:00:00.000 - 2017-11-15 23:59:59.999] 3 DAY, 30.00 y.e.}
		 */
		Tc11,

		/**
		 * Tc12 {[2017-11-16 00:00:00.000 - 2017-11-18 23:59:59.999] 3 DAY, 30.00 y.e.}
		 */
		Tc12,

		/**
		 * Tc13 {[2017-11-19 00:00:00.000 - 2017-11-21 23:59:59.999] 3 DAY, 30.00 y.e.}
		 */
		Tc13,

		/**
		 * Tc14 {[2017-11-22 00:00:00.000 - 2017-11-24 23:59:59.999] 3 DAY, 30.00 y.e.}
		 */
		Tc14,

		/**
		 * Tc15 {[2017-11-25 00:00:00.000 - 2017-11-27 23:59:59.999] 3 DAY, 30.00 y.e.}
		 */
		Tc15,

		/**
		 * Tc16 {[2017-11-28 00:00:00.000 - 2017-11-30 23:59:59.999] 3 DAY, 30.00 y.e.}
		 */
		Tc16,

		/**
		 * 
		 */
		Tc17
	}

}
