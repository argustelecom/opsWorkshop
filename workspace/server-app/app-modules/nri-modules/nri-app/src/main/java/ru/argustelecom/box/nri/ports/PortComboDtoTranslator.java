package ru.argustelecom.box.nri.ports;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.ports.model.PortCombo;

/**
 * b.bazarov 24.04.2018
 */
@DtoTranslator
public class PortComboDtoTranslator  implements DefaultDtoTranslator<PortComboDto,PortCombo> {
	@Override
	public PortComboDto translate(PortCombo businessObject) {
		if (businessObject == null) {
			return null;
		}
		return PortComboDto.builder().id(businessObject.getId())
				.accessTechnology(businessObject.getAccessTechnology())
				.portName(businessObject.getPortName())
				.portNumber(businessObject.getPortNumber())
				.technicalCondition(businessObject.getTechnicalCondition())
				.portPurpose(businessObject.getPortPurpose())
				.macAddress(businessObject.getMacAddress() == null ? null : businessObject.getMacAddress().getMacAddress())
				.transmissionMedium(businessObject.getTransmissionMedium())
				.resourceId(businessObject.getResource().getId())
				.portType(businessObject.getPortType())
				.usageType(businessObject.getUsageType())
				.build();
	}
}
