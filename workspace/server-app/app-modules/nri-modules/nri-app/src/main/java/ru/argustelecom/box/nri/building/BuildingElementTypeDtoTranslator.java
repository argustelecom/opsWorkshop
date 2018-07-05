package ru.argustelecom.box.nri.building;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.building.model.BuildingElementType;

/**
 * Транслятор типа элемента здания в ДТО
 * Created by s.kolyada on 23.08.2017.
 */
@DtoTranslator
public class BuildingElementTypeDtoTranslator
        implements DefaultDtoTranslator<BuildingElementTypeDto, BuildingElementType> {

    /**
     * Перевести объект в DTO
     *
     * @param businessObject объект
     * @return DTO
     */
    @Override
    public BuildingElementTypeDto translate(BuildingElementType businessObject) {
        if (businessObject == null) {
            return null;
        }
        return BuildingElementTypeDto.builder()
                .id(businessObject.getId())
                .name(businessObject.getName())
                .level(businessObject.getLocationLevel())
				.icon(businessObject.getIcon())
                .build();
    }
}
