package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.chargingOf;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToLdt;
import static ru.argustelecom.box.env.stl.period.PeriodType.CALENDARIAN;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;

@RunWith(Parameterized.class)
public class BuilderRoundBoundariesParameterizedTest extends AbstractAccountingTest {

	private TestCase tc;

	public BuilderRoundBoundariesParameterizedTest(TestCase tc) {
		this.tc = tc;
	}

	@Test
	public void shouldRoundBoundaries() {
		InvoicePlanBuilder builder = new InvoicePlanBuilder();
		builder.setChargingPeriod(tc.getCp());
		builder.setRoundingPolicy(tc.getRoundingPolicy());
		builder.setPreviousPlan(tc.getPreviousPlan());
		builder.setPlannedStart(tc.getPlannedStart());
		builder.setPlannedEnd(tc.getPlannedEnd());

		Range<LocalDateTime> result = builder.roundBoundaries();

		assertThat(result, is(notNullValue()));
		assertThat(tc.getCp().contains(result.lowerEndpoint()), is(true));
		assertThat(tc.getCp().contains(result.upperEndpoint()), is(true));
		assertThat(result.lowerEndpoint(), equalTo(tc.getExpectedStart()));
		assertThat(result.upperEndpoint(), equalTo(tc.getExpectedEnd()));
	}

	@Parameters(name = "{0}")
	public static List<TestCase> getTestCases() {
		//@formatter:off
		
		// В тесте рассматривается только календарный период, т.к. с ним легче воспринимать и контролировать результаты
		// С произвольным периодом будет работать точно также за тем исключением, что базовая единица будет час или 
		// минута вместо дня с началом не в 1970-01-01 00:00:00.000, а в точке интереса. Корректность работы метода 
		// округления (выравнивания по границам БЕ) зависит от корректности работы метода определения этих самых 
		// границ базовой единицы, который, в свою очередь, проверяется другим тестом PeriodUtilsBaseUnitBoundariesTest
		
		// 2017-11-25 00:00:00.000 --- 2017-11-27 23:59:59.999
		ChargingPeriod cp = chargingOf(CALENDARIAN, "2017-11-26 00:00:00.000"); 
		
		return Arrays.asList(
			
			// -------------------------------------------------------------------------------------------------------
			// 1. Фактическое округление не требуется, потому что планируемые даты и так уже находятся на границах 
			// базовой единицы	
			new TestCase("1.1 UP без факт. округления, без сдвига влево до начала БЕ, без компенсации PAST", 
				cp, previousPlan(cp.prev()), RoundingPolicy.UP,
				"2017-11-25 00:00:00.000", "2017-11-27 23:59:59.999", 
				"2017-11-25 00:00:00.000", "2017-11-27 23:59:59.999"
			),
			new TestCase("1.2 DOWN без факт. округления, без сдвига влево до начала БЕ, без компенсации PAST", 
				cp, previousPlan(cp.prev()), RoundingPolicy.DOWN,
				"2017-11-25 00:00:00.000", "2017-11-27 23:59:59.999", 
				"2017-11-25 00:00:00.000", "2017-11-27 23:59:59.999"
			),

			// -------------------------------------------------------------------------------------------------------
			// 2. Должно быть выполнено фактическое округление, но без сдвига влево до начала БЕ и компенсации. Левая граница планируемого 
			// периода уже находится в левой границе базовой единицы, правая граница планируемого периода находится 
			// где-то в правой базовой единице не на ее границах	
			new TestCase("2.1 UP c факт. округлением, без сдвига влево до начала БЕ, без компенсации PAST", 
				cp, previousPlan(cp.prev()), RoundingPolicy.UP,
				"2017-11-25 00:00:00.000", "2017-11-27 12:00:00.000", 
				"2017-11-25 00:00:00.000", "2017-11-27 23:59:59.999"
			),
			new TestCase("2.2 DOWN c факт. округлением, без сдвига влево до начала БЕ, без компенсации PAST", 
				cp, previousPlan(cp.prev()), RoundingPolicy.DOWN,
				"2017-11-25 00:00:00.000", "2017-11-27 12:00:00.000", 
				"2017-11-25 00:00:00.000", "2017-11-26 23:59:59.999"
			),

			// -------------------------------------------------------------------------------------------------------
			// 3. Должно быть выполнено смещение влево до начала базовой единицы, т.к. левая граница планируемого периода 
			// находится не в начале базовой единицы. При этом длина временного отрезка равна целому количеству базовых 
			// единиц. В этом случае после выравнивания по границам начальной базовой единицы фактического округления не 
			// требуется
			new TestCase("3.1 UP без факт. округления, со сдвигом влево до начала БЕ, без компенсации PAST", 
				cp, previousPlan(cp.prev()), RoundingPolicy.UP,
				"2017-11-25 12:00:00.000", "2017-11-27 11:59:59.999", 
				"2017-11-25 00:00:00.000", "2017-11-26 23:59:59.999"
			),
			new TestCase("3.2 DOWN без факт. округления, со сдвигом влево до начала БЕ, без компенсации PAST", 
				cp, previousPlan(cp.prev()), RoundingPolicy.DOWN,
				"2017-11-25 12:00:00.000", "2017-11-27 11:59:59.999", 
				"2017-11-25 00:00:00.000", "2017-11-26 23:59:59.999"
			),

			// -------------------------------------------------------------------------------------------------------
			// 4. После выравнивания по левой границе начальной базовой единицы окончание временного отрезка находится 
			// где-то в пределах конечной базовой единицы, но не на ее границах. В этом случае должно быть выполнено 
			// и смещение и округление остаточного хвоста	
			new TestCase("4.1 UP c факт. округлением, со сдвигом влево до начала БЕ, без компенсации PAST", 
				cp, previousPlan(cp.prev()), RoundingPolicy.UP,
				"2017-11-25 12:00:00.000", "2017-11-27 13:00:00.000", 
				"2017-11-25 00:00:00.000", "2017-11-27 23:59:59.999"
			),
			new TestCase("4.2 DOWN с факт. округлением, со сдвигом влево до начала БЕ, без компенсации PAST", 
				cp, previousPlan(cp.prev()), RoundingPolicy.DOWN,
				"2017-11-25 12:00:00.000", "2017-11-27 13:00:00.000", 
				"2017-11-25 00:00:00.000", "2017-11-26 23:59:59.999"
			),

			// -------------------------------------------------------------------------------------------------------
			// 5. Начало планируемого периода находится где-то в пределах базовой единицы, которая уже была захвачена 
			// другим периодом в прошлом. В этом случае должна быть выполнена компенсация уже оплаченной базовой единицы
			// и округление по правой границе базовой единицы окончания планируемого периода. При этом компенсация 
			// исключает смещение, т.к. после компенсации левая граница планируемого периода будет выровнена по границе 
			// базовой единицы
			new TestCase("5.1 UP c факт. округлением, без сдвига влево до начала БЕ, c компенсацией PAST", 
				cp, previousPlan(cp, "2017-11-25 00:00:00.000", "2017-11-25 23:59:59.999"), RoundingPolicy.UP,
				"2017-11-25 12:00:00.000", "2017-11-27 13:00:00.000", 
				"2017-11-26 00:00:00.000", "2017-11-27 23:59:59.999"
			),
			new TestCase("5.2 DOWN с факт. округлением, без сдвига влево до начала БЕ, с компенсацией PAST", 
				cp, previousPlan(cp, "2017-11-25 00:00:00.000", "2017-11-25 23:59:59.999"), RoundingPolicy.DOWN,
				"2017-11-25 12:00:00.000", "2017-11-27 13:00:00.000", 
				"2017-11-26 00:00:00.000", "2017-11-26 23:59:59.999"
			),
			// Кейс от Маши
			new TestCase("5.3 DOWN с факт. округлением, без сдвига влево до начала БЕ, с компенсацией PAST", 
				cp, previousPlan(cp, "2017-11-25 00:00:00.000", "2017-11-25 23:59:59.999"), RoundingPolicy.DOWN,
				"2017-11-25 00:00:00.001", "2017-11-26 23:59:59.998", 
				"2017-11-26 00:00:00.000", "2017-11-26 00:00:00.000"
			),
			new TestCase("5.4 UP с факт. округлением, без сдвига влево до начала БЕ, с компенсацией PAST",
				cp, previousPlan(cp, "2017-11-25 00:00:00.000", "2017-11-25 23:59:59.999"), RoundingPolicy.UP,
				"2017-11-25 00:00:00.001", "2017-11-26 23:59:59.998",
				"2017-11-26 00:00:00.000", "2017-11-26 23:59:59.999"
			),
			
			// -------------------------------------------------------------------------------------------------------
			// 6. Начало планируемого периода находится где-то в пределах базовой единицы, для которой существует 
			// "отрезок нулевой длины" одного из двух типов (левый или правый). Должна быть выполнена компенсация 
			// базовой единицы уже захваченной в прошлом "отрезком нулевой длины"
			new TestCase("6.1 UP c факт. округлением, без сдвига влево до начала БЕ, c компенсацией PAST отрезком нулевой длины (zeroOnRight)", 
				cp, previousPlan(cp, "2017-11-25 23:59:59.999", "2017-11-25 23:59:59.999"), RoundingPolicy.UP,
				"2017-11-25 12:00:00.000", "2017-11-27 13:00:00.000", 
				"2017-11-26 00:00:00.000", "2017-11-27 23:59:59.999"
			),
			new TestCase("6.2 UP c факт. округлением, без сдвига влево до начала БЕ, c компенсацией PAST отрезком нулевой длины (zeroOnLeft)", 
				cp, previousPlan(cp, "2017-11-26 00:00:00.000", "2017-11-26 00:00:00.000"), RoundingPolicy.UP,
				"2017-11-25 12:00:00.000", "2017-11-27 13:00:00.000", 
				"2017-11-26 00:00:00.000", "2017-11-27 23:59:59.999"
			),
			new TestCase("6.3 DOWN c факт. округлением, без сдвига влево до начала БЕ, c компенсацией PAST отрезком нулевой длины (zeroOnRight)", 
				cp, previousPlan(cp, "2017-11-25 23:59:59.999", "2017-11-25 23:59:59.999"), RoundingPolicy.DOWN,
				"2017-11-25 12:00:00.000", "2017-11-27 13:00:00.000", 
				"2017-11-26 00:00:00.000", "2017-11-26 23:59:59.999"
			),
			new TestCase("6.4 DOWN c факт. округлением, без сдвига влево до начала БЕ, c компенсацией PAST отрезком нулевой длины (zeroOnLeft)", 
				cp, previousPlan(cp, "2017-11-26 00:00:00.000", "2017-11-26 00:00:00.000"), RoundingPolicy.DOWN,
				"2017-11-25 12:00:00.000", "2017-11-27 13:00:00.000", 
				"2017-11-26 00:00:00.000", "2017-11-26 23:59:59.999"
			),

			// -------------------------------------------------------------------------------------------------------
			// 7. Различные варианты округления планируемого периода, который начинается и заканчивается в пределах одной
			// базовой единицы
			new TestCase("7.1 UP c факт. округлением, со сдвигом влево до начала БЕ, без компенсации PAST, в пределах одной БЕ",  
				cp, previousPlan(cp.prev()), RoundingPolicy.UP,
				"2017-11-25 10:00:00.000", "2017-11-25 20:00:00.000", 
				"2017-11-25 00:00:00.000", "2017-11-25 23:59:59.999"
			),
			new TestCase("7.2 DOWN с факт. округлением, со сдвигом влево до начала БЕ, без компенсации PAST, в пределах одной БЕ", 
				cp, previousPlan(cp.prev()), RoundingPolicy.DOWN,
				"2017-11-25 10:00:00.000", "2017-11-25 20:00:00.000", 
				"2017-11-25 00:00:00.000", "2017-11-25 00:00:00.000"
			),
			new TestCase("7.3 UP c факт. округлением, без сдвига влево до начала БЕ, без компенсации PAST, в пределах одной БЕ",  
				cp, previousPlan(cp.prev()), RoundingPolicy.UP,
				"2017-11-25 00:00:00.000", "2017-11-25 20:00:00.000", 
				"2017-11-25 00:00:00.000", "2017-11-25 23:59:59.999"
			),
			new TestCase("7.4 DOWN с факт. округлением, без сдвига влево до начала БЕ, без компенсации PAST, в пределах одной БЕ", 
				cp, previousPlan(cp.prev()), RoundingPolicy.DOWN,
				"2017-11-25 00:00:00.000", "2017-11-25 20:00:00.000", 
				"2017-11-25 00:00:00.000", "2017-11-25 00:00:00.000"
			),
			
			// -------------------------------------------------------------------------------------------------------
			// 8. Поглощение планируемого периода, находящогося в пределах одной тарифицированной ранее базовой единицы
			new TestCase("8.1 UP c факт. округлением, со сдвигом влево до начала БЕ, c компенсацией PAST, в пределах одной БЕ",  
				cp, previousPlan(cp, "2017-11-25 00:00:00.000", "2017-11-25 23:59:59.999"), RoundingPolicy.UP,
				"2017-11-25 10:00:00.000", "2017-11-25 20:00:00.000", 
				"2017-11-25 23:59:59.999", "2017-11-25 23:59:59.999"
			),
			new TestCase("8.2 DOWN с факт. округлением, со сдвигом влево до начала БЕ, c компенсацией PAST, в пределах одной БЕ", 
				cp, previousPlan(cp, "2017-11-25 00:00:00.000", "2017-11-25 23:59:59.999"), RoundingPolicy.DOWN,
				"2017-11-25 10:00:00.000", "2017-11-25 20:00:00.000", 
				"2017-11-25 23:59:59.999", "2017-11-25 23:59:59.999"
			),
			
			// -------------------------------------------------------------------------------------------------------
			// 9. Округление планируемого периода, находящогося в пределах одной тарифицированной ранее базовой единицы, 
			// захваченной "отрезком нулевой длины" в различных комбинациях 
			new TestCase("9.1 UP c факт. округлением, со сдвигом влево до начала БЕ, c компенсацией PAST отрезком zeroOnRight, в пределах одной БЕ",  
				cp, previousPlan(cp, "2017-11-25 23:59:59.999", "2017-11-25 23:59:59.999"), RoundingPolicy.UP,
				"2017-11-25 10:00:00.000", "2017-11-25 20:00:00.000", 
				"2017-11-25 23:59:59.999", "2017-11-25 23:59:59.999"
			),
			new TestCase("9.2 UP c факт. округлением, со сдвигом влево до начала БЕ, c компенсацией PAST отрезком zeroOnLeft, в пределах одной БЕ",  
				cp, previousPlan(cp, "2017-11-25 00:00:00.000", "2017-11-25 00:00:00.000"), RoundingPolicy.UP,
				"2017-11-25 10:00:00.000", "2017-11-25 20:00:00.000", 
				"2017-11-25 00:00:00.000", "2017-11-25 23:59:59.999"
			),
			new TestCase("9.3 DOWN с факт. округлением, со сдвигом влево до начала БЕ, c компенсацией PAST отрезком zeroOnRight, в пределах одной БЕ", 
				cp, previousPlan(cp, "2017-11-25 23:59:59.999", "2017-11-25 23:59:59.999"), RoundingPolicy.DOWN,
				"2017-11-25 10:00:00.000", "2017-11-25 20:00:00.000", 
				"2017-11-25 23:59:59.999", "2017-11-25 23:59:59.999"
			),
			new TestCase("9.4 DOWN с факт. округлением, со сдвигом влево до начала БЕ, c компенсацией PAST отрезком zeroOnLeft, в пределах одной БЕ", 
				cp, previousPlan(cp, "2017-11-25 00:00:00.000", "2017-11-25 00:00:00.000"), RoundingPolicy.DOWN,
				"2017-11-25 10:00:00.000", "2017-11-25 20:00:00.000", 
				"2017-11-25 00:00:00.000", "2017-11-25 00:00:00.000"
			)
		);
		//@formatter:on
	}

	static class TestCase extends AbstractAccountingParameterizedTestCase {

		private InvoicePlan previousPlan;
		private LocalDateTime plannedStart;
		private LocalDateTime plannedEnd;
		private RoundingPolicy roundingPolicy;
		private LocalDateTime expectedStart;
		private LocalDateTime expectedEnd;

		public TestCase(String name, ChargingPeriod cp, InvoicePlan previousPlan, RoundingPolicy roundingPolicy,
				String plannedStart, String plannedEnd, String expectedStart, String expectedEnd) {
			super(name, cp);
			this.previousPlan = previousPlan;
			this.roundingPolicy = roundingPolicy;
			this.plannedStart = strToLdt(plannedStart);
			this.plannedEnd = strToLdt(plannedEnd);
			this.expectedStart = strToLdt(expectedStart);
			this.expectedEnd = strToLdt(expectedEnd);
		}

		public InvoicePlan getPreviousPlan() {
			return previousPlan;
		}

		public LocalDateTime getPlannedStart() {
			return plannedStart;
		}

		public LocalDateTime getPlannedEnd() {
			return plannedEnd;
		}

		public RoundingPolicy getRoundingPolicy() {
			return roundingPolicy;
		}

		public LocalDateTime getExpectedStart() {
			return expectedStart;
		}

		public LocalDateTime getExpectedEnd() {
			return expectedEnd;
		}
	}
}
