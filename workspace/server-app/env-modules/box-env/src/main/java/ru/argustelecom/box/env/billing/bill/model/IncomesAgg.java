package ru.argustelecom.box.env.billing.bill.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.AggData;

/**
 * Агрегированные данные по поступлениям/остаткам ({@link IncomesRaw}).
 */
@Getter
@Setter
@NoArgsConstructor
public class IncomesAgg extends AggData {

	@Builder
	public IncomesAgg(Long analyticTypeId, String keyword, BigDecimal sum, AnalyticTypeError error) {
		super(analyticTypeId, keyword, sum, error);
	}

	@Override
	public BigDecimal getMathSum() {
		return sum;
	}
}
