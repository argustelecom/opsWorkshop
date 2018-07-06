package ru.argustelecom.box.nri.ports;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.ports.model.PortOpticTransceiver;

/**
 * b.bazarov 26.04.2018
 */
@DtoTranslator
public class PortOpticTransceiverDtoTranslator  implements DefaultDtoTranslator<PortOpticTransceiverDto,PortOpticTransceiver> {


	@Override
	public PortOpticTransceiverDto translate(PortOpticTransceiver businessObject) {
		if (businessObject == null) {
			return null;
		}
		return PortOpticTransceiverDto.builder().id(businessObject.getId())
				.accessTechnology(businessObject.getAccessTechnology())
				.portName(businessObject.getPortName())
				.portNumber(businessObject.getPortNumber())
				.technicalCondition(businessObject.getTechnicalCondition())
				.portPurpose(businessObject.getPortPurpose())
				.medium(businessObject.getTransmissionMedium())
				.resourceId(businessObject.getResource().getId())
				.macAddress(businessObject.getMacAddress() == null ? null : businessObject.getMacAddress().getMacAddress())
				.waveLength(businessObject.getWaveLength())
				.serialNum(businessObject.getSerialNum())
				.formFactor(businessObject.getFormFactor())
				.build();
	}
}