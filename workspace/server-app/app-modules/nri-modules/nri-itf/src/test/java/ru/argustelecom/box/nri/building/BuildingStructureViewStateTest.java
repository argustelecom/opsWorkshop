package ru.argustelecom.box.nri.building;

import org.junit.Test;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.nri.building.model.BuildingElement;

import static org.junit.Assert.*;

/**
 * Created by s.kolyada on 09.09.2017.
 */
public class BuildingStructureViewStateTest {

	private BuildingStructureViewState viewState = new BuildingStructureViewState();

	@Test
	public void shouldInitialize() throws Exception {
		viewState.setBuildingElement(BuildingElement.builder().id(1L).build());
		viewState.setLocation(new Building(2L));

		assertNotNull(viewState.getBuildingElement());
		assertNotNull(viewState.getLocation());
	}
}