package ru.argustelecom.box.nri.building;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.building.nls.ResourceInstallationVMMessagesBundle;
import ru.argustelecom.box.nri.coverage.ResourceInstallationAppService;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDtoTranslator;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author a.wisniewski
 * @since 31.08.2017
 */
@Named(value = "resourceInstallationVM")
@PresentationModel
public class ResourceInstallationViewModel extends ViewModel {

	private static final long serialVersionUID = 7515330126464593030L;

	/**
	 * Точка монтирования
	 */
	@Getter
	@Setter
	private ResourceInstallationDto installation;

	/**
	 * Строение
	 */
	@Getter
	@Setter
	private BuildingElementDto building;

	/**
	 * Сервис точки монтирования
	 */
	@Inject
	private ResourceInstallationAppService installationService;

	/**
	 * Сервис элементов строений
	 */
	@Inject
	private BuildingElementAppService buildingService;

	/**
	 * состояние вьюшки
	 */
	@Inject
	private ResourceInstallationViewState viewState;

	/**
	 * Транслятор точек монтирования
	 */
	@Inject
	private ResourceInstallationDtoTranslator installationDtoTranslator;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		ResourceInstallation inst = viewState.getInstallation();

		if (inst == null) {
			throw new BusinessException(LocaleUtils.getMessages(ResourceInstallationVMMessagesBundle.class).installationDoesNotSet());
		}

		inst = EntityManagerUtils.initializeAndUnproxy(inst);

		installation = installationDtoTranslator.translate(inst);

		if (installation.getInstalledAt() != null)
			building = buildingService.findBuildingByResInstallation(installation);

		unitOfWork.makePermaLong();
	}

	/**
	 * Слушатель события изменения комментария
	 */
	public void changedComment() {
		installationService.updateInstallationComment(installation, installation.getComment());
	}
}
