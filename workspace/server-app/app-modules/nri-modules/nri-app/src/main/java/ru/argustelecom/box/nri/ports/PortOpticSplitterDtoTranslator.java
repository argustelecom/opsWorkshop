package ru.argustelecom.box.nri.ports;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.ports.model.PortOpticSplitter;

/**
 * b.bazarov 26.04.2018
 */
@DtoTranslator
public class PortOpticSplitterDtoTranslator  implements DefaultDtoTranslator<PortOpticSplitterDto,PortOpticSplitter> {


	@Override
	public PortOpticSplitterDto translate(PortOpticSplitter businessObject) {
		if (businessObject == null) {
			return null;
		}
		return PortOpticSplitterDto.builder().id(businessObject.getId())
				.accessTechnology(businessObject.getAccessTechnology())
				.portName(businessObject.getPortName())
				.portNumber(businessObject.getPortNumber())
				.technicalCondition(businessObject.getTechnicalCondition())
				.portPurpose(businessObject.getPortPurpose())
				.medium(businessObject.getTransmissionMedium())
				.resourceId(businessObject.getResource().getId())
				.role(businessObject.getRole())
				.connectorType(businessObject.getConnectorType())
				.build();
	}
}
