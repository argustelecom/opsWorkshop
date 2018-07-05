package ru.argustelecom.box.env.billing.bill.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.AggData;

/**
 * Вычисления итоговых значений по сырым данным.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(value = { "mathSum", "sumToPay", "actualSum" })
public class Summary extends AggData {

	private Boolean invertible;

	@Builder
	public Summary(Long analyticTypeId, String keyword, BigDecimal sum, Boolean invertible, AnalyticTypeError error) {
		super(analyticTypeId, keyword, sum, error);
		this.invertible = invertible;
	}

	@Override
	public BigDecimal getMathSum() {
		return sum;
	}

	@Override
	public BigDecimal getSum() {
		return sum;
	}

	@Override
	public BigDecimal getActualSum() {
		return invertible == null || invertible ? sum.negate() : sum;
	}

	/**
	 * На основании суммарной аналитики формируется {@linkplain Bill#totalAmount итоговая сумма к оплате} счёта. В
	 * случае если {@linkplain #getSum() сумма} положительна, то понимаем, что клиент ничего платить не должен (у него
	 * наоборот избыток средств) и возращаем нулевое значение иначе возвращаем инвертированное значение суммы.
	 */
	public BigDecimal getSumToPay() {
		return getSum().compareTo(BigDecimal.ZERO) == 1 ? BigDecimal.ZERO : getSum().negate();
	}
}
