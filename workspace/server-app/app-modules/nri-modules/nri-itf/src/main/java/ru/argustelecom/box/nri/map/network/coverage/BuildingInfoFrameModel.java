package ru.argustelecom.box.nri.map.network.coverage;

import lombok.Getter;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.map.page.CurrentMapObject;
import ru.argustelecom.box.nri.coverage.ResourceInstallationAppService;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Named(value = "buildingInfoForMapFrameModel")
@PresentationModel
public class BuildingInfoFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private CurrentMapObject currentMapObject;

	@Inject
	private ResourceInstallationAppService resourceInstallationAppService;

	public List<ResourceInstallationDto> getInstallations() {
		if (currentMapObject == null || currentMapObject.isNull()) {
			return null;
		}

		Building building = currentMapObject.getValueAsBuilding();

		return resourceInstallationAppService.findAllByBuilding(building);
	}
}
