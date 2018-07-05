package ru.argustelecom.box.env.billing.invoice.model;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsFirst;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = { "dateFrom", "dateTo", "tariffId", "serviceId", "processingStage"}, callSuper = false)
public class FilterAggData {
	private Date dateFrom;
	private Date dateTo;
	private Long tariffId;
	private Long serviceId;
	private String processingStage;
	private String cause;

	public static Comparator<FilterAggData> entryDataComparator() {
		return comparing(FilterAggData::getTariffId, nullsFirst(Long::compareTo))
				.thenComparing(FilterAggData::getServiceId, nullsFirst(Long::compareTo))
				.thenComparing(FilterAggData::getProcessingStage, nullsFirst(String::compareTo));
	}
}
