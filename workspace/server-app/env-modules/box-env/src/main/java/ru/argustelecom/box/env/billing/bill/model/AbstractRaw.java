package ru.argustelecom.box.env.billing.bill.model;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Родитель для классов, описывающих сырые данные счёта.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "analyticTypeId")
public class AbstractRaw {

	/**
	 * Идентификатор типа аналитики.
	 */
	private Long analyticTypeId;

	/**
	 * Ошика при формировании аналитики.
	 */
	private AnalyticTypeError error;

	public AbstractRaw(Long analyticTypeId, AnalyticTypeError error) {
		this.analyticTypeId = analyticTypeId;
		this.error = error;
	}

	/**
	 * @return Если при расчёте аналитики не возникло ошибок({@link AnalyticTypeError}), то вернёт <b>True</b>
	 */
	@JsonIgnore
	public boolean isValid() {
		return error == null;
	}

	public static Comparator<AbstractRaw> abstractRawComparator() {
		return Comparator.comparing(AbstractRaw::getError, Comparator.nullsFirst(AnalyticTypeError::compareTo))
				.thenComparing(AbstractRaw::getAnalyticTypeId);
	}
}