package ru.argustelecom.box.nri.building;

import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.system.inf.page.PresentationState;

import java.io.Serializable;

/**
 * Состояние вьюхи для инсталляции ресурса
 * Created by s.kolyada on 06.09.2017.
 */
@PresentationState
public class ResourceInstallationViewState implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Расположение элемента строения
	 */
	private ResourceInstallation installation;

	public ResourceInstallation getInstallation() {
		return installation;
	}

	public void setInstallation(ResourceInstallation installation) {
		this.installation = installation;
	}
}
