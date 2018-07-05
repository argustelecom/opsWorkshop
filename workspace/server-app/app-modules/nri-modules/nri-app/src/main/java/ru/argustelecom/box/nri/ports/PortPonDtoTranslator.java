package ru.argustelecom.box.nri.ports;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.ports.model.PortPon;


/**
 * b.bazarov 26.04.2018
 */
@DtoTranslator
public class PortPonDtoTranslator  implements DefaultDtoTranslator<PortPonDto,PortPon> {


	@Override
	public PortPonDto translate(PortPon businessObject) {
		if (businessObject == null) {
			return null;
		}
		return PortPonDto.builder().id(businessObject.getId())
				.accessTechnology(businessObject.getAccessTechnology())
				.portName(businessObject.getPortName())
				.portNumber(businessObject.getPortNumber())
				.technicalCondition(businessObject.getTechnicalCondition())
				.portPurpose(businessObject.getPortPurpose())
				.medium(businessObject.getTransmissionMedium())
				.resourceId(businessObject.getResource().getId())
				.connectorType(businessObject.getConnectorType())
				.maxSubscriberNum(businessObject.getMaxSubscriberNum())
				.build();
	}
}
