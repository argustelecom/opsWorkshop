package ru.argustelecom.box.nri.logicalresources.ip.subnet;

import lombok.Getter;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Фрейм инфо о подсети
 *
 * @author d.khekk
 * @since 13.12.2017
 */
@Named(value = "ipSubnetFM")
@PresentationModel
public class IPSubnetFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис для работы с подсетями
	 */
	@Inject
	private IPSubnetAppService service;

	/**
	 * Выбранная подсеть
	 */
	@Getter
	private IPSubnetDto subnet;

	/**
	 * Действия после открытия страницы
	 *
	 * @param subnet подсеть
	 */
	public void preRender(IPSubnetDto subnet) {
		this.subnet = subnet;
	}

	/**
	 * Изменить комментарий подсети
	 */
	public void changeComment() {
		service.changeComment(subnet);
	}
}
