package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Optional.ofNullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.AbstractBillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.AnalyticTypeError;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.ChargesAgg;
import ru.argustelecom.box.env.billing.bill.model.ChargesRaw;
import ru.argustelecom.box.env.billing.bill.model.ChargesType;
import ru.argustelecom.box.env.billing.bill.model.IncomesAgg;
import ru.argustelecom.box.env.billing.bill.model.IncomesRaw;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.service.DomainService;

@DomainService
public class AggregationService implements Serializable {

	private static final long serialVersionUID = -1452651524828616470L;

	@PersistenceContext
	private EntityManager em;

	public List<ChargesAgg> aggregateCharges(List<ChargesRaw> chargesRawList) {
		checkArgument(chargesRawList != null && !chargesRawList.isEmpty());

		Stream<ChargesRaw> billEntriesRaw = chargesRawList.stream().filter(ChargesRaw::isRow);
		Map<BillEntriesRawGroupping, List<ChargesRaw>> billEntriesRawGroups = billEntriesRaw.collect(
				Collectors.groupingBy(billEntryRaw -> new BillEntriesRawGroupping(billEntryRaw.getAnalyticTypeId(),
						billEntryRaw.getSubjectId())));
		Stream<ChargesAgg> bilEntriesStream = billEntriesRawGroups.entrySet().stream().map(this::createBillEntry);

		Stream<ChargesRaw> analyticsRaw = chargesRawList.stream().filter(chargesRaw -> !chargesRaw.isRow());
		Map<Long, List<ChargesRaw>> analyticsRawGroups = analyticsRaw
				.collect(Collectors.groupingBy(ChargesRaw::getAnalyticTypeId));
		Stream<ChargesAgg> analyticsStream = analyticsRawGroups.entrySet().stream().map(this::createChargesAnalytic);

		List<ChargesAgg> chargesAggs = Stream.concat(bilEntriesStream, analyticsStream).collect(Collectors.toList());

		chargesAggs.sort(ChargesAgg.chargesAggComparator());

		return chargesAggs;
	}

	public List<IncomesAgg> aggregateIncomes(List<IncomesRaw> incomesRawList) {
		checkArgument(incomesRawList != null && !incomesRawList.isEmpty());

		Map<Long, List<IncomesRaw>> analyticsRawGroups = incomesRawList.stream()
				.collect(Collectors.groupingBy(IncomesRaw::getAnalyticTypeId));

		List<IncomesAgg> incomesAggs = analyticsRawGroups.entrySet().stream().map(this::createIncomesAnalytic)
				.collect(Collectors.toList());

		incomesAggs.sort(AggData.aggDataComparator());

		return incomesAggs;
	}

	private ChargesAgg createBillEntry(Entry<BillEntriesRawGroupping, List<ChargesRaw>> billEntryRawGroup) {
		BillAnalyticType analyticType = em.find(BillAnalyticType.class, billEntryRawGroup.getKey().getAnalyticTypeId());
		boolean periodic = analyticType.getChargesType().equals(ChargesType.RECURRENT);
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal sumWithoutTax = BigDecimal.ZERO;
		BigDecimal discountSum = BigDecimal.ZERO;
		for (ChargesRaw chargesRaw : billEntryRawGroup.getValue()) {
			sum = sum.add(chargesRaw.getSum());
			sumWithoutTax = sumWithoutTax.add(calcSumWithoutTax(chargesRaw.getSum(), chargesRaw.getTaxRate()));
			discountSum = discountSum.add(ofNullable(chargesRaw.getDiscountSum()).orElse(BigDecimal.ZERO));
		}
		BigDecimal tax = sum.subtract(sumWithoutTax);
		AnalyticTypeError error = billEntryRawGroup.getValue().get(0).getError();
		//@formatter:off
		return ChargesAgg.builder()
					.analyticTypeId(analyticType.getId())
					.keyword(analyticType.getKeyword())
					.subjectId(billEntryRawGroup.getKey().getSubjectId())
					.periodic(periodic)
					.row(true)
					.sum(sum)
					.tax(tax)
					.sumWithoutTax(sumWithoutTax)
					.discountSum(discountSum)
					.error(error)
				.build();
		//@formatter:on
	}

	private ChargesAgg createChargesAnalytic(Entry<Long, List<ChargesRaw>> analyticsRawGroup) {
		AbstractBillAnalyticType analyticType = em.find(AbstractBillAnalyticType.class, analyticsRawGroup.getKey());
		String keyword = analyticType.getKeyword();
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal sumWithoutTax = BigDecimal.ZERO;
		BigDecimal discountSum = BigDecimal.ZERO;
		for (ChargesRaw chargesRaw : analyticsRawGroup.getValue()) {
			sum = sum.add(chargesRaw.getSum());
			sumWithoutTax = sumWithoutTax.add(calcSumWithoutTax(chargesRaw.getSum(), chargesRaw.getTaxRate()));
			discountSum = discountSum.add(chargesRaw.getDiscountSum());
		}
		BigDecimal tax = sum.subtract(sumWithoutTax);
		AnalyticTypeError error = analyticsRawGroup.getValue().get(0).getError();

		// @formatter:off
		return ChargesAgg.builder()
				.analyticTypeId(analyticType.getId())
				.keyword(keyword)
				.row(false)
				.periodic(false)
				.sum(sum)
				.sumWithoutTax(sumWithoutTax)
				.tax(tax)
				.discountSum(discountSum)
				.error(error)
		.build();
		// @formatter:on
	}

	private IncomesAgg createIncomesAnalytic(Entry<Long, List<IncomesRaw>> analyticsRawGroup) {
		AbstractBillAnalyticType analyticType = em.find(AbstractBillAnalyticType.class, analyticsRawGroup.getKey());
		String keyword = analyticType.getKeyword();
		BigDecimal sum = analyticsRawGroup.getValue().stream().map(IncomesRaw::getSum).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		AnalyticTypeError error = analyticsRawGroup.getValue().get(0).getError();

		return IncomesAgg.builder().analyticTypeId(analyticType.getId()).keyword(keyword).sum(sum).error(error).build();
	}

	private BigDecimal calcSumWithoutTax(BigDecimal sum, BigDecimal taxRate) {
		if (BigDecimal.ZERO.equals(taxRate)) {
			return sum;
		}

		if (BigDecimal.ZERO.equals(sum)) {
			return BigDecimal.ZERO;
		}

		return new Money(sum).divide(BigDecimal.ONE.add(taxRate)).getAmount();
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@EqualsAndHashCode
	static class BillEntriesRawGroupping {
		private Long analyticTypeId;
		private Long subjectId;
	}

}
