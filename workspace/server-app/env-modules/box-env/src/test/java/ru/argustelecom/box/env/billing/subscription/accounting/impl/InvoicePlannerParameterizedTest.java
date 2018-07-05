package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.AbstractAccountingTest.InvoicePlanExpectation.plan;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.AbstractAccountingTest.InvoicePlanPeriodExpectation.period;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.AbstractAccountingTest.SubscriptionSettings.subscription;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlannerParameterizedTest.TestCase.testCase;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVATION_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVE;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.FORMALIZATION;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED_FOR_DEBT;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED_ON_DEMAND;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToLdt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;

@RunWith(Parameterized.class)
public class InvoicePlannerParameterizedTest extends AbstractAccountingTest {

	private TestCase tc;

	public InvoicePlannerParameterizedTest(TestCase tc) {
		this.tc = tc;
	}

	// @formatter:off
	
	@Override
	public void setup() {
		super.setup();

		if (tc.getSubscriptionSettings() != null) {
			tc.getSubscriptionSettings().apply(this.subscription);
			rebuildChargingPeriods();
		}

		tc.getPlannerConfig().setSubscription(this.subscription);

		if (tc.getLastPlanCpid() != null) {
			InvoicePlan lastPlan = planOf(
				tc.getLastPlanCpid(),
				tc.getLastPlanPlannedStart(),
				tc.getLastPlanPlannedEnd(),
				tc.isLastPlanPresent()
			);
			tc.getPlannerConfig().setLastPlan(lastPlan);
		}
	}
	
	@Test
	public void shouldMeetExpectations() {
		InvoicePlanner planner = new InvoicePlanner(tc.getPlannerConfig());
		List<InvoicePlan> result = planner.createFuturePlans();

		assertThat(
			"Количиество запланированных инвойсов соответствует ожидаемому", 
			result.size(), equalTo(tc.getExpectations().size())
		);

		for (int i = 0; i < result.size(); i++) {
			tc.getExpectations().get(i).test(result.get(i), this::cpOf);
		}
	}

	@Parameters(name = "{0}")
	public static List<TestCase> getTestCases() {
		return Arrays.asList(
			
			// ***************************************************************************************************
			testCase("#1. Единственный следующий план при первичной активации из оформления")
			.with(subscription()
				.inState(FORMALIZATION)
			)
			.with(new InvoicePlannerConfig()
				.setAllowPrimaryActivation(true)
			)
			.expects(
				plan("2017-10-15 12:00:00.000", "2017-10-15 23:59:59.999", CPID.Tc01, RoundingPolicy.UP)
					.withSummary(
						period("2017-10-15 00:00:00.000", "2017-10-15 23:59:59.999", 1, "9.68", "9.68", "0")
					)
					.withDetails(
						period("2017-10-15 00:00:00.000", "2017-10-15 23:59:59.999", 1, "9.68", "9.68", "0")
					)
			), 
			
			
			// ***************************************************************************************************
			testCase("#2. Единственный следующий план при первичной активации из ожидания активации")
			.with(subscription()
				.inState(ACTIVATION_WAITING)
			)
			.with(new InvoicePlannerConfig())
			.expects(
				plan("2017-10-15 12:00:00.000", "2017-10-15 23:59:59.999", CPID.Tc01, RoundingPolicy.UP)
					.withSummary(
						period("2017-10-15 00:00:00.000", "2017-10-15 23:59:59.999", 1, "9.68", "9.68", "0")
					)
					.withDetails(
						period("2017-10-15 00:00:00.000", "2017-10-15 23:59:59.999", 1, "9.68", "9.68", "0")
					)
			),
			
			
			// ***************************************************************************************************
			testCase("#3. Несколько планов для активной подписки")
			.with(subscription()
				.inState(ACTIVE)
			)
			.with(new InvoicePlannerConfig()
				.setBoundaries(strToLdt("2017-10-01 00:00:00.000"), strToLdt("2017-10-31 23:59:59.999"))
			)
			.withLastPlan(CPID.Tc04, true)
			.expects(
				plan("2017-10-25 00:00:00.000", "2017-10-27 23:59:59.999", CPID.Tc05)
					.withSummary(
						period("2017-10-25 00:00:00.000", "2017-10-27 23:59:59.999", 3, "29.03", "29.03", "0")
					)
					.withDetails(
						period("2017-10-25 00:00:00.000", "2017-10-27 23:59:59.999", 3, "29.03", "29.03", "0")
					),
				plan("2017-10-28 00:00:00.000", "2017-10-31 23:59:59.999", CPID.Tc06)
					.withSummary(
						period("2017-10-28 00:00:00.000", "2017-10-31 23:59:59.999", 4, "38.71", "38.71", "0")
					)
					.withDetails(
						period("2017-10-28 00:00:00.000", "2017-10-31 23:59:59.999", 4, "38.71", "38.71", "0")
					)	
			),
			
			
			// ***************************************************************************************************
			testCase("#4. Возобновления тарификации для приостановленной подписки")
			.with(subscription()
				.inState(SUSPENDED_FOR_DEBT)
			)
			.with(new InvoicePlannerConfig()
				.setRenewalDate(strToLdt("2017-10-26 12:00:00.000"))	
			)
			.withLastPlan(CPID.Tc04, false)
			.expects(
				plan("2017-10-26 12:00:00.000", "2017-10-27 23:59:59.999", CPID.Tc05, RoundingPolicy.UP)
					.withSummary(
						period("2017-10-26 00:00:00.000", "2017-10-27 23:59:59.999", 2, "19.35", "19.35", "0")
					)
					.withDetails(
						period("2017-10-26 00:00:00.000", "2017-10-27 23:59:59.999", 2, "19.35", "19.35", "0")
					)
			),
			
			
			// ***************************************************************************************************			
			testCase("#5. Возобновления тарификации для приостановленной подписки в последней уже " +
			         "протарифицированной БЕ (проверяем, что не возьмем деньги за уже оплаченную БЕ)")
			.with(subscription()
				.inState(SUSPENDED_ON_DEMAND)
			)
			.with(new InvoicePlannerConfig()
				.setRenewalDate(strToLdt("2017-10-24 01:03:24.325"))	
			)
			.withLastPlan(CPID.Tc04, "2017-10-22 00:00:00.000", "2017-10-24 00:01:38.437", false)
			.expects(
				plan("2017-10-24 01:03:24.325", "2017-10-24 23:59:59.999", CPID.Tc04, RoundingPolicy.UP)
					.withSummary(
						period("2017-10-24 23:59:59.999", "2017-10-24 23:59:59.999", 0, "0", "0", "0")
					)
					.withDetails(
						period("2017-10-24 23:59:59.999", "2017-10-24 23:59:59.999", 0, "0", "0", "0")
					)
			),
			
			
			// ***************************************************************************************************
			testCase("#6.1. Генерация планов для срочной подписки должна быть приостановлена по достижению validTo")
			.with(subscription()
				.inState(ACTIVE)
				.validTo("2017-10-28 00:00:00.000")
			)
			.with(new InvoicePlannerConfig()
				.setBoundaries(strToLdt("2017-10-01 00:00:00.000"), strToLdt("2017-10-31 23:59:59.999"))
			)
			.withLastPlan(CPID.Tc04, true)
			.expects(
				plan("2017-10-25 00:00:00.000", "2017-10-27 23:59:59.999", CPID.Tc05)
					.withSummary(
						period("2017-10-25 00:00:00.000", "2017-10-27 23:59:59.999", 3, "29.03", "29.03", "0")
					)
					.withDetails(
						period("2017-10-25 00:00:00.000", "2017-10-27 23:59:59.999", 3, "29.03", "29.03", "0")
					)
			),
			
			
			// ***************************************************************************************************
			testCase("#6.2. Генерация планов для срочной подписки должна быть приостановлена по достижению validTo")
			.with(subscription()
				.inState(ACTIVE)
				.validTo("2017-10-27 12:00:00.000")
			)
			.with(new InvoicePlannerConfig()
				.setBoundaries(strToLdt("2017-10-01 00:00:00.000"), strToLdt("2017-10-31 23:59:59.999"))
			)
			.withLastPlan(CPID.Tc04, true)
			.expects(
				plan("2017-10-25 00:00:00.000", "2017-10-27 12:00:00.000", CPID.Tc05)
					.withSummary(
						period("2017-10-25 00:00:00.000", "2017-10-27 23:59:59.999", 3, "29.03", "29.03", "0")
					)
					.withDetails(
						period("2017-10-25 00:00:00.000", "2017-10-27 23:59:59.999", 3, "29.03", "29.03", "0")
					)
			),
			
			
			// ***************************************************************************************************
			testCase("#7.1. Должны подхватить модификатор периода, на который приходится начало инвойса и " +
			         "который разобьет цельный период инвойса на несколько")
			.with(subscription()
				.inState(ACTIVE)
			)
			.with(new InvoicePlannerConfig()
				.setBoundaries(strToLdt("2017-10-01 00:00:00.000"), strToLdt("2017-10-31 23:59:59.999"))
				.addPeriodModifier(
					complexModifier("2017-10-25 00:00:00.000", "2017-10-26 12:00:00.000", "Доверительный", "1")
				)
			)
			.withLastPlan(CPID.Tc04, true)
			.expects(
				plan("2017-10-25 00:00:00.000", "2017-10-26 12:00:00.000", CPID.Tc05)
					.withSummary(
						period("2017-10-25 00:00:00.000", "2017-10-26 23:59:59.999", 2, "19.35", "19.35", "0")
					)
					.withDetails(
						period("2017-10-25 00:00:00.000", "2017-10-26 23:59:59.999", 2, "19.35", "19.35", "0")
					),
				plan("2017-10-26 12:00:00.001", "2017-10-27 23:59:59.999", CPID.Tc05)
					.withSummary(
						period("2017-10-27 00:00:00.000", "2017-10-27 23:59:59.999", 1, "9.68", "9.68", "0")
					)
					.withDetails(
						period("2017-10-27 00:00:00.000", "2017-10-27 23:59:59.999", 1, "9.68", "9.68", "0")
					),
				plan("2017-10-28 00:00:00.000", "2017-10-31 23:59:59.999", CPID.Tc06)
					.withSummary(
						period("2017-10-28 00:00:00.000", "2017-10-31 23:59:59.999", 4, "38.71", "38.71", "0")
					)
					.withDetails(
						period("2017-10-28 00:00:00.000", "2017-10-31 23:59:59.999", 4, "38.71", "38.71", "0")
					)	
			),
			
			
			// ***************************************************************************************************
			testCase("#7.2. Должны проигнорировать модификатор периода, начинающийся и заканчивающийся в " + 
			         "середине периода списания")
			.with(subscription()
				.inState(ACTIVE)
			)
			.with(new InvoicePlannerConfig()
				.setBoundaries(strToLdt("2017-10-01 00:00:00.000"), strToLdt("2017-10-31 23:59:59.999"))
				.addPeriodModifier(
					complexModifier(
						"2017-10-25 12:00:00.000", "2017-10-26 11:59:59.999", "Доверительный но бесполезный", "1"
					)
				)
			)
			.withLastPlan(CPID.Tc04, true)
			.expects(
				plan("2017-10-25 00:00:00.000", "2017-10-27 23:59:59.999", CPID.Tc05)
					.withSummary(
						period("2017-10-25 00:00:00.000", "2017-10-27 23:59:59.999", 3, "29.03", "29.03", "0")
					)
					.withDetails(
						period("2017-10-25 00:00:00.000", "2017-10-27 23:59:59.999", 3, "29.03", "29.03", "0")
					),
				plan("2017-10-28 00:00:00.000", "2017-10-31 23:59:59.999", CPID.Tc06)
					.withSummary(
						period("2017-10-28 00:00:00.000", "2017-10-31 23:59:59.999", 4, "38.71", "38.71", "0")
					)
					.withDetails(
						period("2017-10-28 00:00:00.000", "2017-10-31 23:59:59.999", 4, "38.71", "38.71", "0")
					)	
			),
			
			
			// ***************************************************************************************************
			testCase("#8. Должны успешно завершить планирвание, если последний инвойс завершился после окончания " +
					 "периода интереса")
			.with(subscription()
				.inState(ACTIVE)
			)
			.with(new InvoicePlannerConfig()
				.setBoundaries(strToLdt("2017-10-01 00:00:00.000"), strToLdt("2017-10-23 23:59:59.999"))
			)
			.withLastPlan(CPID.Tc04, true)
			.expects(
				//NO RESULT
			) 
		);
	}

	public static class TestCase extends AbstractAccountingParameterizedTestCase {

		private SubscriptionSettings subscriptionSettings;
		private InvoicePlannerConfig plannerConfig;
		private List<InvoicePlanExpectation> expectations = new ArrayList<>();
		private CPID lastPlanCpid;
		private boolean lastPlanPresent;
		private LocalDateTime lastPlanPlannedStart;
		private LocalDateTime lastPlanPlannedEnd;

		protected TestCase(String name, ChargingPeriod cp) {
			super(name, cp);
		}

		public SubscriptionSettings getSubscriptionSettings() {
			return subscriptionSettings;
		}

		public InvoicePlannerConfig getPlannerConfig() {
			return plannerConfig;
		}

		public List<InvoicePlanExpectation> getExpectations() {
			return expectations;
		}
		
		public CPID getLastPlanCpid() {
			return lastPlanCpid;
		}

		public boolean isLastPlanPresent() {
			return lastPlanPresent;
		}
		
		public LocalDateTime getLastPlanPlannedStart() {
			return lastPlanPlannedStart;
		}

		public LocalDateTime getLastPlanPlannedEnd() {
			return lastPlanPlannedEnd;
		}

		public TestCase with(SubscriptionSettings subscriptionSettings) {
			this.subscriptionSettings = subscriptionSettings;
			return this;
		}

		public TestCase with(InvoicePlannerConfig plannerConfig) {
			this.plannerConfig = plannerConfig;
			return this;
		}
		
		public TestCase withLastPlan(CPID cpid, boolean present) {
			this.lastPlanCpid = cpid;
			this.lastPlanPresent = present;
			this.lastPlanPlannedStart = null;
			this.lastPlanPlannedEnd = null;
			return this;
		}
		
		public TestCase withLastPlan(CPID cpid, String plannedStart, String plannedEnd, boolean present) {
			this.lastPlanCpid = cpid;
			this.lastPlanPresent = present;
			this.lastPlanPlannedStart = strToLdt(plannedStart);
			this.lastPlanPlannedEnd = strToLdt(plannedEnd);
			return this;
		}

		public TestCase expects(InvoicePlanExpectation... expectations) {
			for (InvoicePlanExpectation plan : expectations) {
				this.expectations.add(plan);
			}
			return this;
		}

		public static TestCase testCase(String name) {
			return new TestCase(name, null);
		}
	}

}
