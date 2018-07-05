package ru.argustelecom.box.env.telephony.tariff;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry.TariffEntryStatus;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import static java.util.stream.Collectors.joining;
import static ru.argustelecom.box.env.telephony.tariff.HasPrefixes.DEFAULT_PREFIX_DELIMITER;

@Getter
@Setter
@NoArgsConstructor
public class TariffEntryDto extends ConvertibleDto {
	private Long id;
	private String name;
	private Money chargePerUnit;
	private BusinessObjectDto<TelephonyZone> zone;
	private TariffEntryStatus status;
	private List<Integer> prefixes;
	private List<TariffEntryHistoryDto> history;
	private Long version;

	@Builder
	public TariffEntryDto(Long id, String name, Money chargePerUnit, BusinessObjectDto<TelephonyZone> zone,
			TariffEntryStatus status, List<Integer> prefixes, List<TariffEntryHistoryDto> history, Long version) {
		this.id = id;
		this.name = name;
		this.chargePerUnit = chargePerUnit;
		this.zone = zone;
		this.status = status;
		this.prefixes = prefixes;
		this.history = history;
		this.version = version;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return TariffEntryDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return TariffEntry.class;
	}


	public String getPrefixAsString() {
		return getPrefixes().stream().map(Object::toString).collect(joining(DEFAULT_PREFIX_DELIMITER));
	}
}
