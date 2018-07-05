package ru.argustelecom.box.nri.logicalresources.ip.subnet;

import lombok.Getter;
import ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressDtoTmp;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Фрейм инфо об IP адресах
 *
 * @author d.khekk
 * @since 13.12.2017
 */
@Named(value = "ipAddressFM")
@PresentationModel
public class IPAddressFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Список IP адресов подсети
	 */
	@Getter
	private List<IPAddressDtoTmp> ipAddresses;

	/**
	 * Действия после открытия фрейма
	 *
	 * @param subnetDto подсеть с IP адресами
	 */
	public void preRender(IPSubnetDto subnetDto) {
		//TODO заменить на настоящий дто
		this.ipAddresses = new ArrayList<>();
		subnetDto.getIpAddresses().forEach(dto ->  this.ipAddresses.add(new IPAddressDtoTmp(dto)));
	}
}
