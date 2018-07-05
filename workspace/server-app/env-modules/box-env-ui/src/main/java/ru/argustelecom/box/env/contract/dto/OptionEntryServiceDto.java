package ru.argustelecom.box.env.contract.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class OptionEntryServiceDto extends ConvertibleDto {

	private Long id;
	private String name;
	private String contractName;

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Service.class;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return OptionEntryServiceDtoTranslator.class;
	}

}
