package ru.argustelecom.box.env.address;

import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class BuildingDtoTranslator implements DefaultDtoTranslator<BuildingDto, Building> {

	@Override
	public BuildingDto translate(Building building) {
		return new BuildingDto(building.getId(), building.getObjectName(), building.getFullName());
	}

}