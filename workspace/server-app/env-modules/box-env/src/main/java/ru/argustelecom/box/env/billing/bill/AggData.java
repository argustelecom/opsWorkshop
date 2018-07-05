package ru.argustelecom.box.env.billing.bill;

import java.math.BigDecimal;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.AnalyticTypeError;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "analyticTypeId", "keyword", "error" })
@JsonIgnoreProperties(value = { "mathSum", "actualSum" })
public abstract class AggData {

	protected Long analyticTypeId;
	protected String keyword;
	protected BigDecimal sum;
	protected AnalyticTypeError error;

	protected AggData(Long analyticTypeId, String keyword, BigDecimal sum, AnalyticTypeError error) {
		this.analyticTypeId = analyticTypeId;
		this.keyword = keyword;
		this.sum = sum;
		this.error = error;
	}

	@JsonIgnore
	public boolean isValid() {
		return error == null;
	}

	public abstract BigDecimal getMathSum();

	/**
	 * Возвращает значение суммы в актуальном для конечного пользователя виде.
	 * <ul>
	 * <li>Для поступлений и остатков это значение равняется хранимой сумме ({@link #sum}).</li>
	 * <li>Для начислений значение суммы ({@link #sum}) должно инвертироваться.</li>
	 * </ul>
	 */
	public BigDecimal getActualSum() {
		return sum;
	}

	public static Comparator<AggData> aggDataComparator() {
		return Comparator.comparing(AggData::getAnalyticTypeId, Comparator.nullsFirst(Long::compareTo))
				.thenComparing(AggData::getKeyword, Comparator.nullsFirst(String::compareTo))
				.thenComparing(AggData::getError, Comparator.nullsFirst(AnalyticTypeError::compareTo));
	}

}
