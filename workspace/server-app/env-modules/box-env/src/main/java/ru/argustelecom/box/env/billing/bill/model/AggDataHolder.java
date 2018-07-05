package ru.argustelecom.box.env.billing.bill.model;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.argustelecom.box.env.billing.bill.AggData;

/**
 * Врапер агрегированных данных необходимых для формирования счёта. Объединяет:
 * <ul>
 * <li>{@link ChargesAgg}</li>
 * <li>{@link IncomesAgg}</li>
 * <li>{@link Summary}</li>
 * </ul>
 */
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "billEntries", "analytics" })
public class AggDataHolder {
	@NonNull
	private List<Long> subscriptionIdList;
	@NonNull
	private List<ChargesAgg> chargesAggList;
	@NonNull
	private List<IncomesAgg> incomesAggList;
	@NonNull
	private List<Summary> summaries;

	/**
	 * Возвращает не модифицируемый список идентификаторов подписок, по которым построен счет
	 */
	public List<Long> getSubscriptionIdList() {
		return unmodifiableList(subscriptionIdList);
	}

	/**
	 * Возвращает не модифицируемый список агрегированных сырых данных: по аналитикам поступления/остатки.
	 */
	public List<IncomesAgg> getIncomesAggList() {
		return unmodifiableList(incomesAggList);
	}

	/**
	 * Возвращает не модифицируемый список сырых данных: итоговых аналитик.
	 */
	public List<Summary> getSummaries() {
		return unmodifiableList(summaries);
	}

	/**
	 * Возвращает не модифицируемый список агрегированных сырых данных: по аналитикам начисления.
	 */
	public List<ChargesAgg> getChargesAggList() {
		return unmodifiableList(chargesAggList);
	}

	public List<ChargesAgg> getBillEntries() {
		return getChargesAggList().stream().filter(ChargesAgg::isRow).collect(Collectors.toList());
	}

	public List<AggData> getAnalytics() {
		List<AggData> result = new ArrayList<>();
		result.addAll(getChargesAggList().stream()
				.filter(chargesAgg -> !chargesAgg.isRow() && chargesAgg.getKeyword() != null)
				.collect(Collectors.toList()));
		result.addAll(getIncomesAggList().stream().filter(incomesAgg -> incomesAgg.getKeyword() != null)
				.collect(Collectors.toList()));
		result.addAll(getSummaries().stream().filter(summary -> summary.getKeyword() != null)
				.collect(Collectors.toList()));
		return result;
	}
}
