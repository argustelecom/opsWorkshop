package ru.argustelecom.box.env.billing.bill.model;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сырые данные по поступлениям и остаткам денежных средст на лицевой счёт.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "personalAccountId", "date", "sum" }, callSuper = true)
public class IncomesRaw extends AbstractRaw {

	private Long personalAccountId;
	private Long transactionId;
	private Date date;
	private BigDecimal sum;

	@Builder
	public IncomesRaw(Long analyticTypeId, Long personalAccountId, Long transactionId, Date date, BigDecimal sum,
			AnalyticTypeError error) {
		super(analyticTypeId, error);
		this.personalAccountId = personalAccountId;
		this.transactionId = transactionId;
		this.date = date;
		this.sum = sum;
	}

	public static Comparator<IncomesRaw> incomesRawComparator() {
		return Comparator.comparing(IncomesRaw::getPersonalAccountId, Comparator.nullsFirst(Long::compareTo))
				.thenComparing(IncomesRaw::getDate, Comparator.nullsFirst(Date::compareTo))
				.thenComparing(AbstractRaw.abstractRawComparator());
	}
}
