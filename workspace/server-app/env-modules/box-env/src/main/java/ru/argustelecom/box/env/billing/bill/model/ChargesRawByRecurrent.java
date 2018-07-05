package ru.argustelecom.box.env.billing.bill.model;

import static ru.argustelecom.box.env.billing.bill.model.ChargesType.RECURRENT;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "subscriptionId" }, callSuper = true)
public class ChargesRawByRecurrent extends ChargesRaw {

	private Long subscriptionId;

	@Builder
	public ChargesRawByRecurrent(Long analyticTypeId, Long productId, Long subscriptionId, BigDecimal taxRate,
			Date startDate, Date endDate, BigDecimal sum, BigDecimal discountSum, boolean row,
			AnalyticTypeError error) {
		super(analyticTypeId, productId, RECURRENT, taxRate, startDate, endDate, sum, discountSum, row, error);
		this.subscriptionId = subscriptionId;
	}

}
