package ru.argustelecom.box.env.billing.bill.model;

import static ru.argustelecom.box.env.billing.bill.model.ChargesType.NONRECURRENT;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ChargesRawByNonRecurrent extends ChargesRaw {

	@Builder
	public ChargesRawByNonRecurrent(Long analyticTypeId, Long productId, BigDecimal taxRate, Date startDate,
			Date endDate, BigDecimal sum, BigDecimal discountSum, boolean row, AnalyticTypeError error) {
		super(analyticTypeId, productId, NONRECURRENT, taxRate, startDate, endDate, sum, discountSum, row, error);
	}

}
