package ru.argustelecom.box.env.billing.invoice.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Comparator;

import static java.util.Comparator.*;
import static java.util.Comparator.nullsFirst;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = { "resourceNumber", "telephonyZoneId", "tariffId"}, callSuper = false)
public class UsageInvoiceEntryData {
	private String resourceNumber;
	private Long telephonyZoneId;
	private Long tariffId;
	private BigDecimal amount;

	public static Comparator<UsageInvoiceEntryData> entryDataComparator() {
		return comparing(UsageInvoiceEntryData::getResourceNumber, nullsFirst(String::compareTo))
				.thenComparing(UsageInvoiceEntryData::getTelephonyZoneId, nullsFirst(Long::compareTo))
				.thenComparing(UsageInvoiceEntryData::getTariffId, nullsFirst(Long::compareTo));
	}
}
