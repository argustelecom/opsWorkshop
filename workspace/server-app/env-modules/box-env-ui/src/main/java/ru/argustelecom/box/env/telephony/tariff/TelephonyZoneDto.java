package ru.argustelecom.box.env.telephony.tariff;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class TelephonyZoneDto extends ConvertibleDto {

	private Long id;
	private String name;
	private String description;

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return TelephonyZone.class;
	}
	@Override
	
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return TelephonyZoneDtoTranslator.class;
	}

}
