package ru.argustelecom.box.env.telephony.tariff;

import static lombok.AccessLevel.PROTECTED;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.stl.Money;

/**
 * Базовый класс для результатов работы с классами трафика.
 * 
 * @see TariffEntryImportResult
 * @see TariffEntryQueryResult
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = { "name", "prefixes", "chargePerUnit", "zoneName" })
public abstract class TariffEntryBaseResult implements HasPrefixes {
	private String name;
	private List<Integer> prefixes;
	private Money chargePerUnit;
	private String zoneName;
}
