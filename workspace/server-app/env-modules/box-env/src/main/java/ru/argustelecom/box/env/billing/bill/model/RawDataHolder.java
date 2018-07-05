package ru.argustelecom.box.env.billing.bill.model;

import static java.util.Collections.unmodifiableList;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Врапер сырых данных необходимых для формирования счёта. Объединяет:
 * <ul>
 * <li>{@link ChargesRaw}</li>
 * <li>{@link IncomesRaw}</li>
 * </ul>
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = { "chargesRawList", "incomesRawList" })
public class RawDataHolder {
	@NonNull
	private List<ChargesRaw> chargesRawList;
	@NonNull
	private List<IncomesRaw> incomesRawList;

	/**
	 * Возвращает не модифицируемый список сырых данных: по аналитикам начисления.
	 */
	public List<ChargesRaw> getChargesRawList() {
		return unmodifiableList(chargesRawList);
	}

	/**
	 * Возвращает не модифицируемый список сырых данных: по аналитикам поступления/остатки.
	 */
	public List<IncomesRaw> getIncomesRawList() {
		return unmodifiableList(incomesRawList);
	}

}