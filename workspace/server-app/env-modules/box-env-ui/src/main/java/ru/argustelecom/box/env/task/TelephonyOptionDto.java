package ru.argustelecom.box.env.task;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@EqualsAndHashCode(of = { "id" }, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class TelephonyOptionDto extends ConvertibleDto {

	private Long id;
	private String name;
	private BusinessObjectDto<AbstractTariff> tariff;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return AssigneeDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return TelephonyOption.class;
	}
}