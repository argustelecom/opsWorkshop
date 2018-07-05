package ru.argustelecom.box.nri.building;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.building.model.BuildingElement;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Iterables.isEmpty;
import static java.util.stream.Collectors.toList;

/**
 * Транслятор элемента здания в ДТО
 * Created by s.kolyada on 23.08.2017.
 */
@DtoTranslator
public class BuildingElementDtoTranslator
		implements DefaultDtoTranslator<BuildingElementDto, BuildingElement> {

	/**
	 * Транслятор типа элемента
	 */
	@Inject
	private BuildingElementTypeDtoTranslator typeTranslator;

	@Override
	public BuildingElementDto translate(BuildingElement businessObject) {
		if (businessObject == null) {
			return null;
		}
		return BuildingElementDto.builder()
				.id(businessObject.getId())
				.location(businessObject.getLocation())
				.name(businessObject.getName())
				.type(typeTranslator.translate(businessObject.getType()))
				.childElements(translateAll(businessObject.getChildren()))
				.isRoot(businessObject.getParent() == null)
				.build();
	}

	/**
	 * Трансляция списка элементов
	 * @param buildingElements список элементов строения
	 * @return список ДТО элементов строений
	 */
	public List<BuildingElementDto> translateAll(List<BuildingElement> buildingElements) {
		return isEmpty(buildingElements) ? new ArrayList<>() :
				buildingElements.stream().map(this::translate).collect(toList());
	}
}