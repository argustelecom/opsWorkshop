package ru.argustelecom.box.env.address;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class LocationTypeDto extends ConvertibleDto {

	private Long id;
	private String name;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return null;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return null;
	}

}