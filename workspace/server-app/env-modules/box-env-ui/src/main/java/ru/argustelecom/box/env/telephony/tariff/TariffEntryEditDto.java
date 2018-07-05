package ru.argustelecom.box.env.telephony.tariff;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.empty;
import static ru.argustelecom.box.env.telephony.tariff.HasPrefixes.DEFAULT_PREFIX_DELIMITER;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;

@Getter
@Setter
@NoArgsConstructor
public class TariffEntryEditDto {
	private Long id;
	private String name;
	private String prefix;
	private Money cost;
	private BusinessObjectDto<TelephonyZone> zone;

	@Builder
	public TariffEntryEditDto(Long id, String name, String prefix, Money cost, BusinessObjectDto<TelephonyZone> zone) {
		this.id = id;
		this.name = name;
		this.prefix = prefix;
		this.cost = cost;
		this.zone = zone;
	}

	public List<Integer> getParsedPrefixes() {
		return ofNullable(prefix).map(p -> stream(p.split(DEFAULT_PREFIX_DELIMITER))).orElse(empty())
				.map(Integer::valueOf).collect(toList());
	}
}
