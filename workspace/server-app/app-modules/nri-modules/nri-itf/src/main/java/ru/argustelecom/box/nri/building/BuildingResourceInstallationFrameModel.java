package ru.argustelecom.box.nri.building;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import ru.argustelecom.box.nri.coverage.ResourceInstallationAppService;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Фрейм информации об установленных и покрывающих ресурсах
 *
 * @author d.khekk
 * @since 31.08.2017
 */
@Named(value = "buildingResInstallationFM")
@PresentationModel
@Getter
public class BuildingResourceInstallationFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String PARENT_RESOURCES_TAB_ID = "res_on_parent";

	/**
	 * Сервис для работы с точками монтирования ресурсов
	 */
	@Inject
	private ResourceInstallationAppService service;

	/**
	 * Список точек монтирования, принадлежащих элементу строения
	 */
	private List<ResourceInstallationDto> installedResources;

	/**
	 * Список точек монтирования, принадлежащих строению
	 */
	private List<ResourceInstallationDto> allInstalledResources;

	@Setter
	private ResourceInstallationDto selectedInstallation;

	/**
	 * Список точек монтирования, покрывающих элемент строения
	 */
	private List<ResourceInstallationDto> cover;

	/**
	 * Выбранный элемент строения
	 */
	private BuildingElementDto element;

	/**
	 * Обработка перед выводом страницы
	 *
	 * @param element элемент строения
	 */
	public void preRender(BuildingElementDto element) {
		this.element = element;
		if (element != null) {
			installedResources = service.findAllByBuildingElement(element.getId());
			cover = service.findAllCoveringBuildingElement(element.getId());
			if (element.getIsRoot()) {
				allInstalledResources = service.findAllByParentBuildingElement(element.getId());
			}
		} else {
			installedResources = Collections.emptyList();
			cover = Collections.emptyList();
		}
	}

	/**
	 * Слушатель события выбора инсталляции
	 * Редиректит на страницу с информацией об установке ресусра
	 *
	 * @param event событие
	 * @throws IOException
	 */
	public void installedResourcesRowSelect(SelectEvent event) throws IOException {
		FacesContext.getCurrentInstance()
				.getExternalContext()
				.redirect("ResourceInstallationView.xhtml?installation=ResourceInstallation-"
						+ selectedInstallation.getId());
	}
}
