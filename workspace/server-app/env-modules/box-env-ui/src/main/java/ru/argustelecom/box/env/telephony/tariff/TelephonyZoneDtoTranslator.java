package ru.argustelecom.box.env.telephony.tariff;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class TelephonyZoneDtoTranslator implements DefaultDtoTranslator<TelephonyZoneDto, TelephonyZone> {

	@Override
	public TelephonyZoneDto translate(TelephonyZone telephonyZone) {
		return new TelephonyZoneDto(telephonyZone.getId(), telephonyZone.getName(), telephonyZone.getDescription());
	}

	public List<TelephonyZoneDto> translate(Collection<TelephonyZone> telephonyZones) {
		return telephonyZones.stream().map(this::translate).collect(Collectors.toList());
	}

}
