package ru.argustelecom.box.env.address;

import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class LocationTypeDtoTranslator implements DefaultDtoTranslator<LocationTypeDto, LocationType> {

	@Override
	public LocationTypeDto translate(LocationType locationType) {
		return new LocationTypeDto(locationType.getId(), locationType.getObjectName());
	}

}