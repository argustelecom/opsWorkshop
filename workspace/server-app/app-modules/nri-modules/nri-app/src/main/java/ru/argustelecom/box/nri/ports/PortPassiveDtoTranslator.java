package ru.argustelecom.box.nri.ports;


import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.ports.model.PortPassive;

/**
 * b.bazarov 26.04.2018
 */
@DtoTranslator
public class PortPassiveDtoTranslator implements DefaultDtoTranslator<PortPassiveDto, PortPassive> {


	@Override
	public PortPassiveDto translate(PortPassive businessObject) {
		if (businessObject == null) {
			return null;
		}
		return PortPassiveDto.builder().id(businessObject.getId())
				.accessTechnology(businessObject.getAccessTechnology())
				.portName(businessObject.getPortName())
				.portNumber(businessObject.getPortNumber())
				.technicalCondition(businessObject.getTechnicalCondition())
				.portPurpose(businessObject.getPortPurpose())
				.medium(businessObject.getTransmissionMedium())
				.resourceId(businessObject.getResource().getId())
				.portType(businessObject.getPortType())
				.build();
	}
}
