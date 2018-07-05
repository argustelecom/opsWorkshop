package ru.argustelecom.box.nri.building;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.system.inf.page.PresentationState;

import java.io.Serializable;

/**
 * Состояние вьюхи для структуры строения
 * Created by s.kolyada on 25.08.2017.
 */
@Getter
@Setter
@PresentationState
public class BuildingStructureViewState implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Расположение элемента строения
	 */
	private Location location;

	/**
	 * Элемент строения
	 */
	private BuildingElement buildingElement;
}
