package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.AbstractAccountingTest.InvoicePlanPeriodExpectation.period;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.BuilderCreateInvoicePeriodsParameterizedTest.TestCase.testCase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPriceModifier;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;

@RunWith(Parameterized.class)
public class BuilderCreateInvoicePeriodsParameterizedTest extends AbstractAccountingTest {

	private TestCase tc;

	public BuilderCreateInvoicePeriodsParameterizedTest(TestCase tc) {
		this.tc = tc;
	}

	@Test
	public void shouldMeetExpectations() {
		InvoicePlanBuilder builder = new InvoicePlanBuilder();
		builder.setChargingPeriod(tc.getCp());
		builder.addPriceModifier(tc.getModifiers());

		List<InvoicePlanPeriodImpl> details = builder.createDetails(tc.getBoundaries());

		assertThat(details.size(), equalTo(tc.getDetailExpectations().size()));
		for (int i = 0; i < details.size(); i++) {
			tc.getDetailExpectations().get(i).test(details.get(i));
		}

		InvoicePlanPeriodImpl summary = builder.createSummary(tc.getBoundaries(), details);
		assertThat(summary, is(notNullValue()));
		tc.getSummaryExpectation().test(summary);
	}

	@Parameters(name = "{0}")
	public static List<TestCase> getTestCases() {
		// @formatter:off
		// 2018-01-08 00:00:00.000 --- 2018-01-14 23:59:59.999, 7 БЕ * 10 УЕ = 70 УЕ
		return Arrays.asList(
			
			testCase("#1. Без модификаторов Summary равен единственному Detail и расчитан по стандартным правилам")
			.withModifiers(
				//NO MODIFIERS
			)
			.expectDetails(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "70", "0")
			)
			.expectSummary(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "70", "0")
			),
			
			
			testCase("#2. Один период модификатора приходится на начало тарификации InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-09 23:59:59.999", "Скидка 20%", "0.8")
			)
			.expectDetails(
				period("2018-01-08 00:00:00.000", "2018-01-09 23:59:59.999", 2, "20", "16", "-4"),
				period("2018-01-10 00:00:00.000", "2018-01-14 23:59:59.999", 5, "50", "50", "0")
			)
			.expectSummary(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "66", "-4")
			),
			
			
			testCase("#3. Один период модификатора приходится на середину тарификации InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-11 00:00:00.000", "2018-01-11 23:59:59.999", "Скидка 20%", "0.8")
			)
			.expectDetails(
				period("2018-01-08 00:00:00.000", "2018-01-10 23:59:59.999", 3, "30", "30", "0"),
				period("2018-01-11 00:00:00.000", "2018-01-11 23:59:59.999", 1, "10", "8", "-2"),
				period("2018-01-12 00:00:00.000", "2018-01-14 23:59:59.999", 3, "30", "30", "0")
			)
			.expectSummary(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "68", "-2")
			),
			
			
			testCase("#4. Один период модификатора приходится на конец тарификации InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-12 00:00:00.000", "2018-01-14 23:59:59.999", "Скидка 20%", "0.8")
			)
			.expectDetails(
				period("2018-01-08 00:00:00.000", "2018-01-11 23:59:59.999", 4, "40", "40", "0"),
				period("2018-01-12 00:00:00.000", "2018-01-14 23:59:59.999", 3, "30", "24", "-6")
			)
			.expectSummary(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "64", "-6")
			),
			
			
			testCase("#5. Два периода модификатора приходятся на середину тарификации InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-09 00:00:00.000", "2018-01-10 23:59:59.999", "Скидка 20%", "0.8"),
				priceModifier("2018-01-12 00:00:00.000", "2018-01-13 23:59:59.999", "Скидка 20%", "0.8")
			)
			.expectDetails(
				period("2018-01-08 00:00:00.000", "2018-01-08 23:59:59.999", 1, "10", "10", "0"),
				period("2018-01-09 00:00:00.000", "2018-01-10 23:59:59.999", 2, "20", "16", "-4"),
				period("2018-01-11 00:00:00.000", "2018-01-11 23:59:59.999", 1, "10", "10", "0"),
				period("2018-01-12 00:00:00.000", "2018-01-13 23:59:59.999", 2, "20", "16", "-4"),
				period("2018-01-14 00:00:00.000", "2018-01-14 23:59:59.999", 1, "10", "10", "0")
			)
			.expectSummary(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "62", "-8")
			),
			
			
			testCase("#6. Период модификатора приходятся на весь период тарификации InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "Скидка 20%", "0.8")
			)
			.expectDetails(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "56", "-14")
			)
			.expectSummary(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "56", "-14")
			),
			
			
			testCase("#7. Период абсолютного модификатора приходятся на весь период тарификации InvoicePlan (TestPeriod)")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "Тестовый период", "0")
			)
			.expectDetails(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "0", "-70")
			)
			.expectSummary(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "0", "-70")
			),
			
			
			testCase("#8. Период бесполезного модификатора приходятся на весь период тарификации InvoicePlan (TrustPeriod)")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "Доверительный период", "1")
			)
			.expectDetails(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "70", "0")
			)
			.expectSummary(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "70", "0")
			),


			testCase("#9. Разнонаправленные факторы модификаторов, которые применяются на InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-12 23:59:59.999", "Бонус постоянному клиенту", "-0.1"),
				priceModifier("2018-01-13 00:00:00.000", "2018-01-14 23:59:59.999", "Штраф за просрочку платежа", "1.75")
			)
			.expectDetails(
				period("2018-01-08 00:00:00.000", "2018-01-12 23:59:59.999", 5, "50", "-5", "-55"),
				period("2018-01-13 00:00:00.000", "2018-01-14 23:59:59.999", 2, "20", "35", "15")
			)
			.expectSummary(
				period("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", 7, "70", "30", "-40")
			)
		);
		// @formatter:on
	}

	static class TestCase extends AbstractAccountingParameterizedTestCase {

		private Range<LocalDateTime> boundaries;
		private List<InvoicePlanPriceModifier> modifiers = new ArrayList<>();
		private InvoicePlanPeriodExpectation summaryExpectation;
		private List<InvoicePlanPeriodExpectation> detailExpectations = new ArrayList<>();

		protected TestCase(String name, ChargingPeriod cp, Range<LocalDateTime> boundaries) {
			super(name, cp);
			this.boundaries = boundaries;
		}

		public static TestCase testCase(String name) {
			return new TestCase(name, defaultCp, defaultCp.boundaries());
		}

		public TestCase withModifiers(InvoicePlanPriceModifier... modifiers) {
			for (InvoicePlanPriceModifier modifier : modifiers) {
				this.modifiers.add(modifier);
			}
			return this;
		}

		public TestCase expectSummary(InvoicePlanPeriodExpectation summary) {
			this.summaryExpectation = summary;
			return this;
		}

		public TestCase expectDetails(InvoicePlanPeriodExpectation... details) {
			for (InvoicePlanPeriodExpectation detail : details) {
				this.detailExpectations.add(detail);
			}
			return this;
		}

		public Range<LocalDateTime> getBoundaries() {
			return boundaries;
		}

		public List<InvoicePlanPriceModifier> getModifiers() {
			return modifiers;
		}

		public InvoicePlanPeriodExpectation getSummaryExpectation() {
			return summaryExpectation;
		}

		public List<InvoicePlanPeriodExpectation> getDetailExpectations() {
			return detailExpectations;
		}
	}
}