package ru.argustelecom.box.nri.logicalresources.ip.subnet;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.model.IPSubnet;

import javax.inject.Inject;

import static java.util.stream.Collectors.toList;

/**
 * Транслятор подсетей
 *
 * @author a.wisniewski
 * @since 11.12.2017
 */
@DtoTranslator
public class IPSubnetDtoTranslator implements DefaultDtoTranslator<IPSubnetDto, IPSubnet> {

	@Inject
	private IPAddressDtoTranslator ipTranslator;

	@Override
	public IPSubnetDto translate(IPSubnet ipSubnet) {
		if (ipSubnet == null) {
			return null;
		}
		return IPSubnetDto.builder()
				.id(ipSubnet.getId())
				.name(ipSubnet.getName())
				.subnetType(ipSubnet.getType().toString())
				.comment(ipSubnet.getComment())
				.parentId(ipSubnet.getParent() == null ? null : ipSubnet.getParent().getId())
				.childSubnets(ipSubnet.getChildSubnets().stream().map(this::translate).collect(toList()))
				.ipAddresses(ipSubnet.getIpAddresses().stream().map(ipTranslator::translate).collect(toList()))
				.build();
	}

	/**
	 * Транслировать подсеть без IP-адресов
	 *
	 * @param ipSubnet сущность IP-подсети
	 * @return ДТО IP-подсети
	 */
	public IPSubnetDto translateLazy(IPSubnet ipSubnet) {
		if (ipSubnet == null) {
			return null;
		}
		return IPSubnetDto.builder()
				.id(ipSubnet.getId())
				.name(ipSubnet.getName())
				.subnetType(ipSubnet.getType().toString())
				.parentId(ipSubnet.getParent() == null ? null : ipSubnet.getParent().getId())
				.comment(ipSubnet.getComment())
				.childSubnets(ipSubnet.getChildSubnets().stream().map(this::translateLazy).collect(toList()))
				.build();
	}
}
