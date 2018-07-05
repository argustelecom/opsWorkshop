package ru.argustelecom.box.nri.logicalresources.ip.address;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.IPSubnetDtoTranslator;

import javax.inject.Inject;

/**
 * Транслятор IP-адресов
 *
 * @author d.khekk
 * @since 11.12.2017
 */
@DtoTranslator
public class IPAddressDtoTranslator implements DefaultDtoTranslator<IPAddressDto, IPAddress> {

	@Inject
	private IPSubnetDtoTranslator subnetDtoTranslator;

	@Override
	public IPAddressDto translate(IPAddress ipAddress) {
		if (ipAddress == null) {
			return null;
		}
		return IPAddressDto.builder()
				.id(ipAddress.getId())
				.name(ipAddress.getName())
				.isStatic(ipAddress.getIsStatic())
				.state(ipAddress.getState())
				.stateChangeDate(ipAddress.getStateChangeDate())
				.transferType(ipAddress.getTransferType())
				.isPrivate(ipAddress.getIsPrivate())
				.comment(ipAddress.getComment())
				.subnet(subnetDtoTranslator.translateLazy(ipAddress.getSubnet()))
				.hasBooking(ipAddress.getBookingOrder() != null)
				.build();
	}
}
