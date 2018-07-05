package ru.argustelecom.box.nri.logicalresources.ip.address;

import lombok.Getter;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.nri.loading.ResourceLoadingAppService;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceAppService;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Фрейм для просмотра информации об услуге закрепленной за ip-адресом
 *
 * @author s.kolyada
 * @since 17.01.2018
 */
@Named("ipAddressServiceInfoFrameModel")
@PresentationModel
public class IPAddressServiceInfoFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис работы с логическими ресурсами
	 */
	@Inject
	private LogicalResourceAppService logicalResourceAppService;

	/**
	 * Услуга
	 */
	@Getter
	private Service service;

	/**
	 * Инициализация
	 * @param ip адрес
	 */
	public void preRender(IPAddressDtoTmp ip) {
		if (ip == null) {
			reset();
			return;
		}

		// загружаем услугу
		service = logicalResourceAppService.findService(ip.getId());
	}

	/**
	 * Сбросить все настройки
	 */
	private void reset() {
		service = null;
	}
}
