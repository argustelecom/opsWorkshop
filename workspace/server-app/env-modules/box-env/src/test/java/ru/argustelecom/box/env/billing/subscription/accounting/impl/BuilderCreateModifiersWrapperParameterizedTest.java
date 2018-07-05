package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.BuilderCreateModifiersWrapperParameterizedTest.TestCase.testCase;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.BuilderCreateModifiersWrapperParameterizedTest.WrapperExpectation.modifier;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToLdt;

import java.math.BigDecimal;
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
import ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlanBuilder.PriceModifierWrapper;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;

@RunWith(Parameterized.class)
public class BuilderCreateModifiersWrapperParameterizedTest extends AbstractAccountingTest {

	private TestCase tc;

	public BuilderCreateModifiersWrapperParameterizedTest(TestCase tc) {
		this.tc = tc;
	}

	@Test
	public void shouldMeetExpectations() {
		InvoicePlanBuilder builder = new InvoicePlanBuilder();
		builder.setChargingPeriod(tc.getCp());
		builder.addPriceModifier(tc.getModifiers());

		List<PriceModifierWrapper> wrappers = builder.createModifierWrappers(tc.getBoundaries());
		assertThat(wrappers.size(), equalTo(tc.getExpectations().size()));

		for (int i = 0; i < wrappers.size(); i++) {
			tc.getExpectations().get(i).test(wrappers.get(i));
		}
	}

	@Parameters(name = "{0}")
	public static List<TestCase> getTestCases() {
		//@formatter:off
		
		return Arrays.asList(
			testCase("#01. Период модификатора не входит в период InvoicePlan (слева)")
			.withModifiers(
				priceModifier("2018-01-04 00:00:00.000", "2018-01-05 00:00:00.000")
			)
			.expects(
				// NO RESULT	
			),
			
			testCase("#02. Период модификатора не входит в период InvoicePlan (справа)")
			.withModifiers(
				priceModifier("2018-01-15 00:00:00.000", "2018-01-16 23:59:59.999")
			)
			.expects(
				// NO RESULT	
			),
			
			testCase("#03. Период модификатора пересекается с периодом InvoicePlan (слева)")
			.withModifiers(
				priceModifier("2018-01-07 00:00:00.000", "2018-01-08 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-08 23:59:59.999", "0.7")	
			),
			
			testCase("#04. Период модификатора пересекается с периодом InvoicePlan (справа)")
			.withModifiers(
				priceModifier("2018-01-14 00:00:00.000", "2018-01-15 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-14 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")	
			),
			
			testCase("#05. Рассматривается только период, пересекающийся с периодом InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-04 00:00:00.000", "2018-01-05 00:00:00.000", "0.7"),
				priceModifier("2018-01-14 00:00:00.000", "2018-01-15 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-14 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")	
			),
			
			testCase("#06. Рассматривается только период, пересекающийся с периодом InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-07 00:00:00.000", "2018-01-08 23:59:59.999", "0.7"),
				priceModifier("2018-01-15 00:00:00.000", "2018-01-16 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-08 23:59:59.999", "0.7")	
			),
			
			testCase("#07. Рассматриваются периоды, пересекающиеся с периодом InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-14 00:00:00.000", "2018-01-15 23:59:59.999", "0.7"),
				priceModifier("2018-01-07 00:00:00.000", "2018-01-08 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-08 23:59:59.999", "0.7"),
				modifier("2018-01-14 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")
			),
			
			testCase("#08. Период модификатора входит в период InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-11 00:00:00.000", "2018-01-11 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-11 00:00:00.000", "2018-01-11 23:59:59.999", "0.7")
			),
			
			testCase("#09. Периоды модификатора, пересекающиеся и входящие в период InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-14 00:00:00.000", "2018-01-15 23:59:59.999", "0.7"),
				priceModifier("2018-01-11 00:00:00.000", "2018-01-11 23:59:59.999", "0.7"),
				priceModifier("2018-01-07 00:00:00.000", "2018-01-08 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-08 23:59:59.999", "0.7"),
				modifier("2018-01-11 00:00:00.000", "2018-01-11 23:59:59.999", "0.7"),
				modifier("2018-01-14 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")
			),
			
			testCase("#10. Модификатор с фактором '1' исключается из расчета")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "1")
			)
			.expects(
				// NO RESULT
			), 
			
			testCase("#11. Модификатор с фактором '0' поглощает все остальные модификаторы, с которыми пересекается")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "0")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "0")	
			),
			
			testCase("#12. Модификатор с фактором '0' поглощает все остальные модификаторы, с которыми пересекается")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "0"),
				priceModifier("2018-01-14 00:00:00.000", "2018-01-15 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "0")
			),
			
			testCase("#13. Модификатор с фактором '0' поглощает все остальные модификаторы, с которыми пересекается")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "0"),
				priceModifier("2018-01-14 00:00:00.000", "2018-01-15 23:59:59.999", "0.7"),
				priceModifier("2018-01-11 00:00:00.000", "2018-01-11 23:59:59.999", "0.7"),
				priceModifier("2018-01-07 00:00:00.000", "2018-01-08 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "0")
			),
			
			testCase("#14. Модификатор с фактором '0' частично поглощает период модификатора с произвольным фактором")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-11 23:59:59.999", "0"),
				priceModifier("2018-01-10 00:00:00.000", "2018-01-15 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-11 23:59:59.999", "0"),
				modifier("2018-01-12 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")
			),
			
			testCase("#15. Модификатор с фактором '0' частично поглощает период модификатора с произвольным фактором")
			.withModifiers(
				priceModifier("2018-01-06 00:00:00.000", "2018-01-11 23:59:59.999", "0"),
				priceModifier("2018-01-09 00:00:00.000", "2018-01-15 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-11 23:59:59.999", "0"),
				modifier("2018-01-12 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")
			),
			
			testCase("#16. Граничный вариант слева")
			.withModifiers(
				priceModifier("2018-01-06 00:00:00.000", "2018-01-08 00:00:00.000", "0.7"),
				priceModifier("2018-01-08 00:00:00.000", "2018-01-09 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-09 23:59:59.999", "0.7")
			),
			
			testCase("#17. Граничный вариант справа")
			.withModifiers(
				priceModifier("2018-01-11 00:00:00.000", "2018-01-14 23:59:59.999", "0.7"),
				priceModifier("2018-01-14 23:59:59.999", "2018-01-17 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-11 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")
			),
			
			testCase("#18. Простое округление левой границы")
			.withModifiers(
				priceModifier("2018-01-08 12:00:00.000", "2018-01-09 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-09 00:00:00.000", "2018-01-09 23:59:59.999", "0.7")
			),
			
			testCase("#19. Простое округление правой границы")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-09 12:00:00.000", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-08 23:59:59.999", "0.7")
			),
			
			testCase("#20. Простое округление обеих границ")
			.withModifiers(
				priceModifier("2018-01-08 12:00:00.000", "2018-01-10 12:00:00.000", "0.7")
			)
			.expects(
				modifier("2018-01-09 00:00:00.000", "2018-01-09 23:59:59.999", "0.7")
			),
			
			testCase("#21. Модификатор X заканчивается, а модификатор Y начинается в одной и той же базовой единице")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-10 12:00:00.000", "0.7"),
				priceModifier("2018-01-10 12:00:00.000", "2018-01-14 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-09 23:59:59.999", "0.7"),
				modifier("2018-01-11 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")
			),
			
			testCase("#22. Модификатор начинается и заканчивается в одной базовой единице")
			.withModifiers(
				priceModifier("2018-01-08 10:00:00.000", "2018-01-08 20:00:00.000", "0.7")
			)
			.expects(
				// NO RESULT
			),

			testCase("#23. Модификатор начинается раньше и заканчивается позже InvoicePlan " +
					"(основной случай для большинства InvoicePlan)")
			.withModifiers(
				priceModifier("2018-01-01 00:00:00.000", "2018-02-01 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")
			),

			testCase("#24. Модификатор полностью совпадает с периодом InvoicePlan")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")
			),

			testCase("#25. 2 модификатора идут подряд вплотную")
			.withModifiers(
				priceModifier("2018-01-02 00:00:00.000", "2018-01-08 23:59:59.999","0.7"),
				priceModifier("2018-01-09 00:00:00.000", "2018-01-20 23:59:59.999", "Скидка 50%", "0.5")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-08 23:59:59.999","0.7"),
				modifier("2018-01-09 00:00:00.000", "2018-01-14 23:59:59.999","0.5")
			),

			testCase("#26. Модификатор с фактором, меньшим нуля или больше единицы")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-08 23:59:59.999", "Штраф", "2"),
				priceModifier("2018-01-10 00:00:00.000", "2018-01-11 23:59:59.999", "Подарок миллионному покупателю", "-5")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-08 23:59:59.999", "2"),
				modifier("2018-01-10 00:00:00.000", "2018-01-11 23:59:59.999", "-5")
			),

			testCase("#27. Модификаторы нулевой длины ингорируются")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-08 00:00:00.000"),
				priceModifier("2018-01-14 23:59:59.999", "2018-01-14 23:59:59.999"),
				priceModifier("2018-01-10 00:00:00.000", "2018-01-10 00:00:00.000")
			)
			.expects(
				// NO RESULT
			),

			testCase("#28. Модификаторы нулевой длины с фактором '0' игнорируются")
			.withModifiers(
				priceModifier("2018-01-09 00:00:00.000", "2018-01-09 23:59:59.999"),
				priceModifier("2018-01-09 00:00:00.000", "2018-01-09 00:00:00.000", "0"),
				priceModifier("2018-01-09 10:00:00.000", "2018-01-09 10:00:00.000", "0"),
				priceModifier("2018-01-09 23:59:59.999", "2018-01-09 23:59:59.999", "0")
			)
			.expects(
				modifier("2018-01-09 00:00:00.000", "2018-01-09 23:59:59.999", "0.7")
			),

			testCase("#29. Периоды модификаторов пересекают период InvoicePlan на его концах, но длина пересечения меньше БЕ")
			.withModifiers(
				priceModifier("2018-01-07 00:00:00.000", "2018-01-08 10:59:59.999"),
				priceModifier("2018-01-14 10:00:00.000", "2018-01-20 23:59:59.999")
			)
			.expects(
				// NO RESULT
			),

			testCase("#30. Период модификаторов пересекают границы БЕ, но не захватывает ни одну из них полностью" +
					"(см. также #18. Простое округление обеих границ)")
			.withModifiers(
				priceModifier("2018-01-10 23:00:00.000", "2018-01-11 01:59:59.999")
			)
			.expects(
				// NO RESULT
			),

			testCase("#31. Пересечение нескольких периодов с фактором '0' - " +
					"первый период поглощает общую часть двух периодов")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-11 23:59:59.999", "0"),
				priceModifier("2018-01-11 00:00:00.000", "2018-01-12 23:59:59.999", "0")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-11 23:59:59.999", "0"),
				modifier("2018-01-12 00:00:00.000", "2018-01-12 23:59:59.999", "0")
			),

			testCase("#32. Периодов с фактором '0' полностью содержит другой период с фактором '0' и поглощает его")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-11 23:59:59.999", "0"),
				priceModifier("2018-01-09 00:00:00.000", "2018-01-09 23:59:59.999", "0")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-11 23:59:59.999", "0")
			),
			
			testCase("#33. Нормализация периодов должна прерваться, если периоды модификаторов не пересекаются")
			.withModifiers(
				priceModifier("2018-01-08 00:00:00.000", "2018-01-09 12:00:00.000", "0.7"),
				priceModifier("2018-01-10 00:00:00.000", "2018-01-11 12:00:00.000", "0.7"),
				priceModifier("2018-01-13 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")
			)
			.expects(
				modifier("2018-01-08 00:00:00.000", "2018-01-08 23:59:59.999", "0.7"),
				modifier("2018-01-10 00:00:00.000", "2018-01-10 23:59:59.999", "0.7"),
				modifier("2018-01-13 00:00:00.000", "2018-01-14 23:59:59.999", "0.7")
			)
		);
		
		//@formatter:on
	}

	static class TestCase extends AbstractAccountingParameterizedTestCase {

		private Range<LocalDateTime> boundaries;
		private List<InvoicePlanPriceModifier> modifiers = new ArrayList<>();
		private List<WrapperExpectation> expectations = new ArrayList<>();

		private TestCase(String name, ChargingPeriod cp, Range<LocalDateTime> boundaries) {
			super(name, cp);
			this.boundaries = boundaries;
		}

		public static TestCase testCase(String name) {
			return new TestCase(name, defaultCp, defaultCp.boundaries());
		}

		public static TestCase testCase(String name, ChargingPeriod cp, String lowerBound, String upperBound) {
			return new TestCase(name, cp, Range.closed(strToLdt(lowerBound), strToLdt(upperBound)));
		}

		public TestCase withModifiers(InvoicePlanPriceModifier... modifiers) {
			for (InvoicePlanPriceModifier modifier : modifiers) {
				this.modifiers.add(modifier);
			}
			return this;
		}

		public TestCase expects(WrapperExpectation... expectations) {
			for (WrapperExpectation expectation : expectations) {
				this.expectations.add(expectation);
			}
			return this;
		}

		public Range<LocalDateTime> getBoundaries() {
			return boundaries;
		}

		public List<InvoicePlanPriceModifier> getModifiers() {
			return modifiers;
		}

		public List<WrapperExpectation> getExpectations() {
			return expectations;
		}
	}

	static class WrapperExpectation {
		private LocalDateTime lowerBound;
		private LocalDateTime upperBound;
		private BigDecimal factor;

		public WrapperExpectation(LocalDateTime lowerBound, LocalDateTime upperBound, BigDecimal factor) {
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
			this.factor = factor;
		}

		public void test(PriceModifierWrapper wrapper) {
			assertThat(wrapper.getLowerBound(), equalTo(lowerBound));
			assertThat(wrapper.getUpperBound(), equalTo(upperBound));
			assertThat(wrapper.getModifier().getPriceFactor(), equalTo(factor));
		}

		public static WrapperExpectation modifier(String lowerBound, String upperBound, String factor) {
			return new WrapperExpectation(strToLdt(lowerBound), strToLdt(upperBound), new BigDecimal(factor));
		}
	}
}
