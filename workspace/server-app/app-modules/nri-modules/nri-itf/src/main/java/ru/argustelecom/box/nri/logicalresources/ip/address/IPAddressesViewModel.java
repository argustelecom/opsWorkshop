package ru.argustelecom.box.nri.logicalresources.ip.address;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddressPurpose;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IpTransferType;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.IPSubnetRepository;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.model.IPSubnet;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Контроллер страницы поиска IP-адресов
 * @author d.khekk
 * @since 11.12.2017
 */
@Named(value = "ipaddressesVM")
@PresentationModel
public class IPAddressesViewModel extends ViewModel {

	/**
	 * Ленивый список IP адресов
	 */
	@Inject
	@Getter
	private IPAddressesList lazyIPAddresses;

	/**
	 * Сервис для операций с подсетями
	 */
	@Inject
	private IPSubnetRepository subnetRepository;

	/**
	 * Выбранный IP-адрес
	 */
	@Getter
	@Setter
	private IPAddressDtoTmp selectedIpAddress;

	/**
	 * Список подсетей
	 */
	@Getter
	@Setter
	private List<IPSubnet> subnets;

	/**
	 * Действия после открытия страницы
	 */
	@Override
	@PostConstruct
	protected void postConstruct() {
		subnets = subnetRepository.findAll();
		unitOfWork.makePermaLong();
	}

	/**
	 * Получить все возможные статусы
	 *
	 * @return список возможных статусов
	 */
	public List<IPAddressState> getStates() {
		return asList(IPAddressState.AVAILABLE, IPAddressState.OCCUPIED);
	}

	/**
	 * Получить все возможные методы передачи данных
	 *
	 * @return список возможных методов
	 */
	public List<IpTransferType> getTransferTypes() {
		return IpTransferType.listOfValues();
	}

	/**
	 * Получить все возможные назначения адресов
	 * @return список возможных назначений
	 */
	public List<IPAddressPurpose> getPurposes() {
		return IPAddressPurpose.listOfValues();
	}
}
