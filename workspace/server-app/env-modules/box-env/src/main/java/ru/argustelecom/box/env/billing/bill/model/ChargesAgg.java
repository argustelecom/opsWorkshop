package ru.argustelecom.box.env.billing.bill.model;

import java.math.BigDecimal;
import java.util.Comparator;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.AggData;

/**
 * Агрегированные данные по начислениям ({@link ChargesRaw}).
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "subjectId", "row" }, callSuper = true)
public class ChargesAgg extends AggData {

	/**
	 * Хранит идентификатор опции или продукта
	 */
	private Long subjectId;
	private BigDecimal sumWithoutTax;
	private BigDecimal tax;
	private BigDecimal discountSum;
	private boolean row;
	private boolean periodic;

	@Builder
	public ChargesAgg(Long analyticTypeId, String keyword, BigDecimal sum, Long subjectId, BigDecimal sumWithoutTax,
			BigDecimal tax, BigDecimal discountSum, boolean row, boolean periodic, AnalyticTypeError error) {
		super(analyticTypeId, keyword, sum, error);
		this.subjectId = subjectId;
		this.sumWithoutTax = sumWithoutTax;
		this.tax = tax;
		this.discountSum = discountSum;
		this.row = row;
		this.periodic = periodic;
	}

	@Override
	public BigDecimal getMathSum() {
		return sum.negate();
	}

	public static Comparator<ChargesAgg> chargesAggComparator() {
		return Comparator.comparing(ChargesAgg::getSubjectId, Comparator.nullsFirst(Long::compareTo))
				.thenComparing(ChargesAgg::isRow).thenComparing(AggData.aggDataComparator());
	}
}
