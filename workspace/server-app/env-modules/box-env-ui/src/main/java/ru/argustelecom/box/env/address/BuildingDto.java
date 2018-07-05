package ru.argustelecom.box.env.address;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class BuildingDto extends ConvertibleDto {

	private Long id;
	private String name;
	private String fullName;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return BuildingDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Building.class;
	}

}