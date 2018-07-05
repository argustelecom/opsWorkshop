package ru.argustelecom.box.nri.ports;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.ports.model.PortXDsl;


/**
 * b.bazarov 25.04.2018
 */
@DtoTranslator
public class PortXDslDtoTranslator  implements DefaultDtoTranslator<PortXDslDto,PortXDsl> {
	@Override
	public PortXDslDto translate(PortXDsl businessObject) {
		if (businessObject == null) {
			return null;
		}
		return PortXDslDto.builder().id(businessObject.getId())
				.accessTechnology(businessObject.getAccessTechnology())
				.portName(businessObject.getPortName())
				.portNumber(businessObject.getPortNumber())
				.technicalCondition(businessObject.getTechnicalCondition())
				.portPurpose(businessObject.getPortPurpose())
				.transmissionMedium(businessObject.getTransmissionMedium())
				.resourceId(businessObject.getResource().getId())
				.macAddress(businessObject.getMacAddress() == null ? null : businessObject.getMacAddress().getMacAddress())
				.portType(businessObject.getPortType())
				.build();
	}
}