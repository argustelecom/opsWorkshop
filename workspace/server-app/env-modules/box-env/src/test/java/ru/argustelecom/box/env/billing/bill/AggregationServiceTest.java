package ru.argustelecom.box.env.billing.bill;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static ru.argustelecom.box.env.type.model.TypeCreationalContext.creationalContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.billing.bill.AggregationService.BillEntriesRawGroupping;
import ru.argustelecom.box.env.billing.bill.model.AbstractBillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.AnalyticTypeError;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.ChargesAgg;
import ru.argustelecom.box.env.billing.bill.model.ChargesRaw;
import ru.argustelecom.box.env.billing.bill.model.ChargesRawByNonRecurrent;
import ru.argustelecom.box.env.billing.bill.model.ChargesRawByRecurrent;
import ru.argustelecom.box.env.billing.bill.model.ChargesType;
import ru.argustelecom.box.env.billing.bill.model.IncomesAgg;
import ru.argustelecom.box.env.billing.bill.model.IncomesRaw;
import ru.argustelecom.box.env.product.model.ProductType;
import ru.argustelecom.box.env.product.model.ProductTypeComposite;

@RunWith(MockitoJUnitRunner.class)
public class AggregationServiceTest {

	@Mock
	private EntityManager em;

	@InjectMocks
	private AggregationService aggregationService;

	private List<ChargesRaw> chargesRawList;
	private List<ChargesRaw> chargesRawListWithError;
	private List<IncomesRaw> incomesRawList;
	private List<IncomesRaw> incomesRawListWithError;

	private BillAnalyticType currentPeriodChargesByRecurrent;
	private BillAnalyticType currentPeriodChargesByNonRecurrent;
	private BillAnalyticType previousPeriodCharges;
	private BillAnalyticType nextPeriodCharges;
	private BillAnalyticType currentPeriodStartingBalance;
	private BillAnalyticType currentPeriodIncomes;
	private BillAnalyticType nextPeriodIncomes;
	private AnalyticTypeError error;

	private ProductType internet;
	private ProductTypeComposite internetPlusTv;
	private ProductType router;

	private Long halfMonthInternetSubsId;
	private long monthlyInternetPlusTvSubsId;

	@Before
	public void setup() throws ParseException {
		initDataForRaw();
		initChargesRaw();
		initIncomesRaw();

		Mockito.when(em.find(BillAnalyticType.class, currentPeriodChargesByRecurrent.getId()))
				.thenReturn(currentPeriodChargesByRecurrent);
		Mockito.when(em.find(BillAnalyticType.class, currentPeriodChargesByNonRecurrent.getId()))
				.thenReturn(currentPeriodChargesByNonRecurrent);
		Mockito.when(em.find(AbstractBillAnalyticType.class, previousPeriodCharges.getId()))
				.thenReturn(previousPeriodCharges);
		Mockito.when(em.find(AbstractBillAnalyticType.class, nextPeriodCharges.getId())).thenReturn(nextPeriodCharges);
		Mockito.when(em.find(AbstractBillAnalyticType.class, currentPeriodIncomes.getId()))
				.thenReturn(currentPeriodIncomes);
		Mockito.when(em.find(AbstractBillAnalyticType.class, nextPeriodIncomes.getId())).thenReturn(nextPeriodIncomes);
		Mockito.when(em.find(AbstractBillAnalyticType.class, currentPeriodStartingBalance.getId()))
				.thenReturn(currentPeriodStartingBalance);
	}

	@Test
	public void shouldAggregateCharges() {
		List<ChargesAgg> chargesAggList = aggregationService.aggregateCharges(chargesRawList);
		assertEquals(5, chargesAggList.size());

		Map<BillEntriesRawGroupping, ChargesAgg> billEntries = chargesAggList.stream().filter(ChargesAgg::isRow)
				.collect(Collectors.toMap(chargesAgg -> new BillEntriesRawGroupping(chargesAgg.getAnalyticTypeId(),
						chargesAgg.getSubjectId()), chargesAgg -> chargesAgg));
		assertEquals(3, billEntries.size());

		ChargesAgg currentPeriodChargesByRecurrentForInternet = billEntries
				.get(new BillEntriesRawGroupping(currentPeriodChargesByRecurrent.getId(), internet.getId()));
		assertNotNull(currentPeriodChargesByRecurrentForInternet);
		// assertNull(previousPeriodChargesForInternet.getKeyword());
		assertTrue(currentPeriodChargesByRecurrentForInternet.isPeriodic());
		assertEquals(internet.getId(), currentPeriodChargesByRecurrentForInternet.getSubjectId());
		assertEqualsMoney(new BigDecimal(260), currentPeriodChargesByRecurrentForInternet.getSum());
		assertEqualsMoney(new BigDecimal(31.77), currentPeriodChargesByRecurrentForInternet.getTax());
		assertEqualsMoney(new BigDecimal(228.23), currentPeriodChargesByRecurrentForInternet.getSumWithoutTax());

		ChargesAgg currentPeriodChargesByRecurrentForInternetPlusTv = billEntries
				.get(new BillEntriesRawGroupping(currentPeriodChargesByRecurrent.getId(), internetPlusTv.getId()));
		assertNotNull(currentPeriodChargesByRecurrentForInternetPlusTv);
		// assertNull(previousPeriodChargesForInternetPlusTv.getKeyword());
		assertTrue(currentPeriodChargesByRecurrentForInternetPlusTv.isPeriodic());
		assertEquals(internetPlusTv.getId(), currentPeriodChargesByRecurrentForInternetPlusTv.getSubjectId());
		assertEqualsMoney(new BigDecimal(170), currentPeriodChargesByRecurrentForInternetPlusTv.getSum());
		assertEqualsMoney(new BigDecimal(19.56), currentPeriodChargesByRecurrentForInternetPlusTv.getTax());
		assertEqualsMoney(new BigDecimal(150.44), currentPeriodChargesByRecurrentForInternetPlusTv.getSumWithoutTax());

		ChargesAgg currentPeriodChargesByNonRecurrentForRouter = billEntries
				.get(new BillEntriesRawGroupping(currentPeriodChargesByNonRecurrent.getId(), router.getId()));
		assertNotNull(currentPeriodChargesByNonRecurrentForRouter);
		// assertNull(nextPeriodChargesForRouter.getKeyword());
		assertFalse(currentPeriodChargesByNonRecurrentForRouter.isPeriodic());
		assertEquals(router.getId(), currentPeriodChargesByNonRecurrentForRouter.getSubjectId());
		assertEqualsMoney(new BigDecimal(500), currentPeriodChargesByNonRecurrentForRouter.getSum());
		assertEqualsMoney(new BigDecimal(83.33), currentPeriodChargesByNonRecurrentForRouter.getTax());
		assertEqualsMoney(new BigDecimal(416.67), currentPeriodChargesByNonRecurrentForRouter.getSumWithoutTax());

		Map<Long, ChargesAgg> analytics = chargesAggList.stream().filter(chargesAgg -> !chargesAgg.isRow())
				.collect(Collectors.toMap(chargesAgg -> chargesAgg.getAnalyticTypeId(), chargesAgg -> chargesAgg));
		assertEquals(2, analytics.size());

		ChargesAgg previousPeriodChargesValue = analytics.get(previousPeriodCharges.getId());
		assertNotNull(previousPeriodChargesValue);
		assertEquals(previousPeriodCharges.getKeyword(), previousPeriodChargesValue.getKeyword());
		assertFalse(previousPeriodChargesValue.isPeriodic());
		assertNull(previousPeriodChargesValue.getSubjectId());
		assertEqualsMoney(new BigDecimal(430), previousPeriodChargesValue.getSum());
		assertEqualsMoney(new BigDecimal(55.42), previousPeriodChargesValue.getTax());
		assertEqualsMoney(new BigDecimal(374.58), previousPeriodChargesValue.getSumWithoutTax());

		ChargesAgg nextPeriodChargesValue = analytics.get(nextPeriodCharges.getId());
		assertNotNull(nextPeriodChargesValue);
		assertEquals(nextPeriodCharges.getKeyword(), nextPeriodChargesValue.getKeyword());
		assertFalse(nextPeriodChargesValue.isPeriodic());
		assertNull(nextPeriodChargesValue.getSubjectId());
		assertEqualsMoney(new BigDecimal(500), nextPeriodChargesValue.getSum());
		assertEqualsMoney(new BigDecimal(83.33), nextPeriodChargesValue.getTax());
		assertEqualsMoney(new BigDecimal(416.67), nextPeriodChargesValue.getSumWithoutTax());
	}

	@Test
	public void shouldAggregateChargesWithError() {
		List<ChargesAgg> chargesAggList = aggregationService.aggregateCharges(chargesRawListWithError);
		assertEquals(2, chargesAggList.size());

		List<ChargesAgg> billEntries = chargesAggList.stream().filter(ChargesAgg::isRow).collect(Collectors.toList());
		assertEquals(1, billEntries.size());

		ChargesAgg billEntry = billEntries.get(0);
		assertEquals(error, billEntry.getError());
		assertEqualsMoney(BigDecimal.ZERO, billEntry.getSum());
		assertEqualsMoney(BigDecimal.ZERO, billEntry.getTax());
		assertEqualsMoney(BigDecimal.ZERO, billEntry.getSumWithoutTax());

		List<ChargesAgg> analytics = chargesAggList.stream().filter(chargesAgg -> !chargesAgg.isRow())
				.collect(Collectors.toList());
		assertEquals(1, analytics.size());

		ChargesAgg analytic = analytics.get(0);
		assertEquals(error, analytic.getError());
		assertEqualsMoney(BigDecimal.ZERO, analytic.getSum());
		assertEqualsMoney(BigDecimal.ZERO, analytic.getTax());
		assertEqualsMoney(BigDecimal.ZERO, analytic.getSumWithoutTax());
	}

	@Test
	public void shouldAggregateIncomes() {
		Map<Long, IncomesAgg> analytics = aggregationService.aggregateIncomes(incomesRawList).stream()
				.collect(Collectors.toMap(incomesAgg -> incomesAgg.getAnalyticTypeId(), incomesAgg -> incomesAgg));
		assertEquals(3, analytics.size());

		IncomesAgg currentPeriodIncomesValue = analytics.get(currentPeriodIncomes.getId());
		assertNotNull(currentPeriodIncomesValue);
		assertEquals(currentPeriodIncomes.getKeyword(), currentPeriodIncomesValue.getKeyword());
		assertEqualsMoney(new BigDecimal(1570.5), currentPeriodIncomesValue.getSum());

		IncomesAgg nextPeriodIncomesValue = analytics.get(nextPeriodIncomes.getId());
		assertNotNull(nextPeriodIncomesValue);
		assertEquals(nextPeriodIncomes.getKeyword(), nextPeriodIncomesValue.getKeyword());
		assertEqualsMoney(new BigDecimal(101.5), nextPeriodIncomesValue.getSum());

		IncomesAgg currentPeriodStartingBalanceValue = analytics.get(currentPeriodStartingBalance.getId());
		assertNotNull(currentPeriodStartingBalanceValue);
		assertEquals(currentPeriodStartingBalance.getKeyword(), currentPeriodStartingBalanceValue.getKeyword());
		assertEqualsMoney(new BigDecimal(1005000), currentPeriodStartingBalanceValue.getSum());
	}

	@Test
	public void shouldAggregateIncomesWithError() {
		List<IncomesAgg> analytics = aggregationService.aggregateIncomes(incomesRawListWithError);
		assertEquals(1, analytics.size());

		IncomesAgg analytic = analytics.get(0);
		assertEquals(error, analytic.getError());
		assertEqualsMoney(BigDecimal.ZERO, analytic.getSum());
	}

	private void initDataForRaw() {
		currentPeriodChargesByRecurrent = new BillAnalyticType(1L);
		currentPeriodChargesByRecurrent.setKeyword("currentPeriodChargesByRecurrent");
		currentPeriodChargesByRecurrent.setChargesType(ChargesType.RECURRENT);
		currentPeriodChargesByRecurrent.setIsRow(true);

		currentPeriodChargesByNonRecurrent = new BillAnalyticType(2L);
		currentPeriodChargesByNonRecurrent.setKeyword("currentPeriodChargesByNonRecurrent");
		currentPeriodChargesByNonRecurrent.setChargesType(ChargesType.NONRECURRENT);
		currentPeriodChargesByNonRecurrent.setIsRow(true);

		previousPeriodCharges = new BillAnalyticType(30L);
		previousPeriodCharges.setKeyword("previousPeriodCharges");
		previousPeriodCharges.setIsRow(false);

		nextPeriodCharges = new BillAnalyticType(40L);
		nextPeriodCharges.setKeyword("nextPeriodCharges");
		nextPeriodCharges.setIsRow(false);

		currentPeriodStartingBalance = new BillAnalyticType(3L);
		currentPeriodStartingBalance.setKeyword("currentPeriodStartingBalance");
		currentPeriodStartingBalance.setIsRow(false);

		currentPeriodIncomes = new BillAnalyticType(4L);
		currentPeriodIncomes.setKeyword("currentPeriodIncomes");
		currentPeriodIncomes.setIsRow(false);

		nextPeriodIncomes = new BillAnalyticType(5L);
		nextPeriodIncomes.setKeyword("nextPeriodIncomes");
		nextPeriodIncomes.setIsRow(false);
		error = AnalyticTypeError.START_PERIOD_DATE_AFTER_END_DATE;

		internet = creationalContext(ProductType.class).createType(10L);
		internetPlusTv = creationalContext(ProductTypeComposite.class).createType(11L);
		router = creationalContext(ProductType.class).createType(12L);

		halfMonthInternetSubsId = 20L;
		monthlyInternetPlusTvSubsId = 21L;
	}

	private void initChargesRaw() {
		chargesRawList = new ArrayList<>();
		//@formatter:off
		chargesRawList.add(ChargesRawByRecurrent.builder()
				.analyticTypeId(currentPeriodChargesByRecurrent.getId())
				.productId(internet.getId())
				.subscriptionId(halfMonthInternetSubsId)
				.row(currentPeriodChargesByRecurrent.getIsRow())
				.sum(new BigDecimal(130))
				.discountSum(BigDecimal.ZERO)
				.taxRate(new BigDecimal(0.17))
			.build());
		chargesRawList.add(ChargesRawByRecurrent.builder()
				.analyticTypeId(currentPeriodChargesByRecurrent.getId())
				.productId(internet.getId())
				.subscriptionId(halfMonthInternetSubsId)
				.row(currentPeriodChargesByRecurrent.getIsRow())
				.sum(new BigDecimal(130))
				.discountSum(BigDecimal.ZERO)
				.taxRate(new BigDecimal(0.11))
			.build());
		chargesRawList.add(ChargesRawByRecurrent.builder()
				.analyticTypeId(currentPeriodChargesByRecurrent.getId())
				.productId(internetPlusTv.getId())
				.subscriptionId(monthlyInternetPlusTvSubsId)
				.row(currentPeriodChargesByRecurrent.getIsRow())
				.sum(new BigDecimal(170))
				.discountSum(BigDecimal.ZERO)
				.taxRate(new BigDecimal(0.13))
			.build());
		chargesRawList.add(ChargesRawByNonRecurrent.builder()
				.analyticTypeId(currentPeriodChargesByNonRecurrent.getId())
				.productId(router.getId())
				.row(currentPeriodChargesByNonRecurrent.getIsRow())
				.sum(new BigDecimal(500))
				.discountSum(BigDecimal.ZERO)
				.taxRate(new BigDecimal(0.2))
			.build());
		chargesRawList.add(ChargesRawByNonRecurrent.builder()
				.analyticTypeId(nextPeriodCharges.getId())
				.productId(router.getId())
				.row(nextPeriodCharges.getIsRow())
				.sum(new BigDecimal(500))
				.discountSum(BigDecimal.ZERO)
				.taxRate(new BigDecimal(0.2))
			.build());
		chargesRawList.add(ChargesRawByRecurrent.builder()
				.analyticTypeId(previousPeriodCharges.getId())
				.productId(internet.getId())
				.row(previousPeriodCharges.getIsRow())
				.sum(new BigDecimal(260))
				.discountSum(BigDecimal.ZERO)
				.taxRate(new BigDecimal(0.16))
			.build());
		chargesRawList.add(ChargesRawByRecurrent.builder()
				.analyticTypeId(previousPeriodCharges.getId())
				.productId(internetPlusTv.getId())
				.row(previousPeriodCharges.getIsRow())
				.sum(new BigDecimal(170))
				.discountSum(BigDecimal.ZERO)
				.taxRate(new BigDecimal(0.13))
			.build());

		chargesRawListWithError = new ArrayList<>();
		chargesRawListWithError.add(ChargesRawByRecurrent.builder()
				.analyticTypeId(currentPeriodChargesByRecurrent.getId())
				.productId(internet.getId())
				.subscriptionId(halfMonthInternetSubsId)
				.row(currentPeriodChargesByRecurrent.getIsRow())
				.sum(BigDecimal.ZERO)
				.discountSum(BigDecimal.ZERO)
				.taxRate(new BigDecimal(0.11))
				.error(error)
			.build());
		chargesRawListWithError.add(ChargesRawByRecurrent.builder()
				.analyticTypeId(previousPeriodCharges.getId())
				.productId(internet.getId())
				.row(previousPeriodCharges.getIsRow())
				.sum(BigDecimal.ZERO)
				.discountSum(BigDecimal.ZERO)
				.taxRate(new BigDecimal(0.11))
				.error(error)
			.build());
		//@formatter:on
	}

	private void initIncomesRaw() {
		incomesRawList = new ArrayList<>();
		//@formatter:off
		incomesRawList.add(IncomesRaw.builder()
				.analyticTypeId(currentPeriodIncomes.getId())
				.sum(new BigDecimal(840))
			.build());
		incomesRawList.add(IncomesRaw.builder()
				.analyticTypeId(currentPeriodIncomes.getId())
				.sum(new BigDecimal(280.50))
			.build());
		incomesRawList.add(IncomesRaw.builder()
				.analyticTypeId(currentPeriodIncomes.getId())
				.sum(new BigDecimal(450))
			.build());
		incomesRawList.add(IncomesRaw.builder()
				.analyticTypeId(nextPeriodIncomes.getId())
				.sum(new BigDecimal(100))
			.build());
		incomesRawList.add(IncomesRaw.builder()
				.analyticTypeId(nextPeriodIncomes.getId())
				.sum(new BigDecimal(1.5))
			.build());
		incomesRawList.add(IncomesRaw.builder()
				.analyticTypeId(currentPeriodStartingBalance.getId())
				.sum(new BigDecimal(1005000))
			.build());

		incomesRawListWithError = new ArrayList<>();
		incomesRawListWithError.add(IncomesRaw.builder()
				.analyticTypeId(currentPeriodIncomes.getId())
				.sum(BigDecimal.ZERO)
				.error(error)
			.build());
		//@formatter:on
	}

	private void assertEqualsMoney(BigDecimal expected, BigDecimal actual) {
		assertEquals(expected.setScale(2, RoundingMode.HALF_EVEN), actual.setScale(2, RoundingMode.HALF_EVEN));
	}
}
