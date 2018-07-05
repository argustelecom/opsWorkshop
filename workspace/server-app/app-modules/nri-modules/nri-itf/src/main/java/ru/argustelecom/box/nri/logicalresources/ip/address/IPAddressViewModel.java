package ru.argustelecom.box.nri.logicalresources.ip.address;

import lombok.Getter;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddressPurpose;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

/**
 * Модель страницы карточки IP-адреса
 *
 * @author d.khekk
 * @since 11.12.2017
 */
@Named(value = "ipaddressVM")
@PresentationModel
public class IPAddressViewModel extends ViewModel {

	private static final long serialVersionUID = 1L;

	/**
	 * Состояние страницы просмотра IP-адреса
	 */
	@Inject
	private IPAddressViewState viewState;

	/**
	 * Сервис для операций над IP-адресами
	 */
	@Inject
	private IPAddressAppService service;

	/**
	 * Транслятор сущностей в ДТО
	 */
	@Inject
	private IPAddressDtoTranslatorTmp translator;

	/**
	 * Выбранный IP-адрес
	 */
	@Getter
	private IPAddressDtoTmp ipAddress;

	/**
	 * Действия после созданя модели
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		ipAddress = translator.translate(viewState.getIpAddress());
		unitOfWork.makePermaLong();
	}

	/**
	 * Получить актуальную сущность IP-адреса
	 *
	 * @return актуальный IP-адрес
	 */
	public IPAddress getRealIpAddress() {
		return viewState.getIpAddress();
	}

	/**
	 * Изменить комментарий IP-адреса
	 */

	public void changeComment() {
		service.changeComment(ipAddress.getReal());
	}//TODO Заменить на настоящий дто

	/**
	 * Изменить назначение
	 */
	public void changePurpose() {
		service.changePurpose(ipAddress.getId(), ipAddress.getPurpose());
	}

	/**
	 * Разрешено ли редактирование назначения текущего адреса
	 * см. BOX-2183
	 * @return истина если разрешено, иначе ложь
	 */
	public Boolean getPurposeEditable() {
		if (IPAddressPurpose.SERVICE.equals(ipAddress.getPurpose())
				|| !IPAddressState.AVAILABLE.equals(ipAddress.getState())
				|| ipAddress.getHasBooking()) {
			return false;
		}
		return true;
	}

	/**
	 * Возможные назначения для адреса, доступные для установки вручном режиме
	 * @return список назначений
	 */
	public List<IPAddressPurpose> getPurposes() {
		return Arrays.asList(IPAddressPurpose.CONFIGURATION, IPAddressPurpose.RESERVED, IPAddressPurpose.NOT_SPECIFIED);
	}

	/**
	 * Выводить ли информацию об услуге
	 * см. BOX-2183
	 * @return истина если выводить, иначе ложь
	 */
	public Boolean renderServiceInfo() {
		return IPAddressPurpose.NOT_SPECIFIED.equals(ipAddress.getPurpose());
	}
}
