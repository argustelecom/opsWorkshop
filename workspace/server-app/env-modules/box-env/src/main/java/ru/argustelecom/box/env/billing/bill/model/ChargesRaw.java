package ru.argustelecom.box.env.billing.bill.model;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сырые данные по начислениям(оплаты подписок/единовременных услуг) для счёта.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "subjectId", "startDate", "endDate", "row", "sum" }, callSuper = true)
@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "chargesType")
@JsonSubTypes({ @Type(value = ChargesRawByRecurrent.class, name = "RECURRENT"),
		@Type(value = ChargesRawByNonRecurrent.class, name = "NONRECURRENT"),
		@Type(value = ChargesRawByUsage.class, name = "USAGE"), })
public abstract class ChargesRaw extends AbstractRaw {

	private Long subjectId;
	private ChargesType chargesType;
	private BigDecimal taxRate;
	private Date startDate;
	private Date endDate;
	private BigDecimal sum;
	private BigDecimal discountSum;
	private boolean row;

	public ChargesRaw(Long analyticTypeId, Long subjectId, ChargesType chargesType, BigDecimal taxRate, Date startDate,
			Date endDate, BigDecimal sum, BigDecimal discountSum, boolean row, AnalyticTypeError error) {
		super(analyticTypeId, error);
		this.subjectId = subjectId;
		this.chargesType = chargesType;
		this.taxRate = taxRate;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sum = sum;
		this.discountSum = discountSum;
		this.row = row;
	}

	public static Comparator<ChargesRaw> chargesRawComparator() {
		//@formatter:off
		return Comparator.comparing(ChargesRaw::getSubjectId, Comparator.nullsFirst(Long::compareTo))
				.thenComparing(ChargesRaw::getChargesType, Comparator.naturalOrder())
				.thenComparing(ChargesRaw::getStartDate, Comparator.nullsFirst(Date::compareTo))
				.thenComparing(ChargesRaw::getEndDate, Comparator.nullsFirst(Date::compareTo))
				.thenComparing(ChargesRaw::isRow)
				.thenComparing(abstractRawComparator());
		//@formatter:on
	}
}