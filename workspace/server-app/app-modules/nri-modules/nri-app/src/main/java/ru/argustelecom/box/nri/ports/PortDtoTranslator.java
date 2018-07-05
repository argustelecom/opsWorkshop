package ru.argustelecom.box.nri.ports;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.ports.model.Port;

/**
 * Транслятор порта
 */
@DtoTranslator
public class PortDtoTranslator implements DefaultDtoTranslator<PortDto, Port> {

	@Override
	public PortDto translate(Port businessObject) {
		if (businessObject == null) {
			return null;
		}

		return new PortDto(
				businessObject.getId(),
				businessObject.getType(),
				businessObject.getPortNumber(),
				businessObject.getPortName(),
				businessObject.getAccessTechnology(),
				businessObject.getPortPurpose(),
				businessObject.getTechnicalCondition(),
				businessObject.getTransmissionMedium(),
				businessObject.getResource().getId()


		);
	}
}
