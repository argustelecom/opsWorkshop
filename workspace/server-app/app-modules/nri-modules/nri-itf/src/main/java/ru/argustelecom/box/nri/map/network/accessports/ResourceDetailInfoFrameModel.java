package ru.argustelecom.box.nri.map.network.accessports;

import lombok.Getter;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.nri.building.BuildingElementAppService;
import ru.argustelecom.box.nri.building.BuildingElementDto;
import ru.argustelecom.box.nri.coverage.ResourceInstallationAppService;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * контролер детальной информации
 */
@Named(value = "resourceDetailInfoFM")
@PresentationModel
public class ResourceDetailInfoFrameModel implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Сервис для работы с инсталяцией
	 */
	@Inject
	private ResourceInstallationAppService resourceInstallationAppService;

	/**
	 * Сервис для работы с элементами здания
	 */
	@Inject
	private BuildingElementAppService buildingElementAppService;


	/**
	 * Зоны охвата строения Входят в зоны охвата ресурсов
	 */
	@Getter
	private int covered;
	/**
	 * Зоны охвата Всего абонентских помещений
	 */
	@Getter
	private int all;

	/**
	 * текущий элемент строения
	 */
	@Getter
	private BuildingElementDto currentBuildingElement;

	/**
	 * Вызывается перед отрисовкой
	 *
	 * @param building здание
	 */
	public void preRender(Building building) {
		currentBuildingElement = null;
		covered = 0;
		all = 0;
		if (building != null && (currentBuildingElement = buildingElementAppService.findElementByLocation(building)) != null) {

			//список инсталяций
			List<ResourceInstallationDto> riList = resourceInstallationAppService.findAllByBuilding(building);
			Set<BuildingElementDto> beSet = new HashSet<>();
			riList.forEach(installation -> {
				if (installation.getCover() != null)
					installation.getCover().forEach(coverage -> {
						countCoverageInBuildingTree(coverage,beSet);
					});
			});
			covered = beSet.size();
			beSet.clear();
			countCoverageInBuildingTree(currentBuildingElement,beSet);
			all = beSet.size();
		}
	}

	/**
	 * Ходим по дереву строения и собираем покрытие
	 * @param coverage покрытие
	 * @param beSet копилка
	 */
	private void countCoverageInBuildingTree(BuildingElementDto coverage,Set<BuildingElementDto> beSet){
		//что бы не было повторов когда два усторойства покрывают один участок
		if (LocationLevel.LODGING.equals(coverage.getType().getLevel().getId()) && !beSet.contains(coverage)) {
			beSet.add(coverage);
		}
		if(coverage.getChildElements() != null){
			//У нас если вклюяается рутовый элемент то и его дети покрываются
			coverage.getChildElements().forEach(element -> countCoverageInBuildingTree(element,beSet));
		}
	}

}
