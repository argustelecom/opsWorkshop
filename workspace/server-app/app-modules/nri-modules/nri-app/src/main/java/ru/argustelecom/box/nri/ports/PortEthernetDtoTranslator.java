package ru.argustelecom.box.nri.ports;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.ports.model.PortEthernet;

/**
 * b.bazarov 23.04.2018
 */
@DtoTranslator
public class PortEthernetDtoTranslator implements DefaultDtoTranslator<PortEthernetDto,PortEthernet> {


	@Override
	public PortEthernetDto translate(PortEthernet businessObject) {
		if (businessObject == null) {
			return null;
		}
		return PortEthernetDto.builder().id(businessObject.getId())
				.accessTechnology(businessObject.getAccessTechnology())
				.portName(businessObject.getPortName())
				.portNumber(businessObject.getPortNumber())
				.technicalCondition(businessObject.getTechnicalCondition())
				.portPurpose(businessObject.getPortPurpose())
				.medium(businessObject.getTransmissionMedium())
				.resourceId(businessObject.getResource().getId())
				.macAddress(businessObject.getMacAddress() == null ? null : businessObject.getMacAddress().getMacAddress())
				.portType(businessObject.getPortType())
				.build();
	}
}
