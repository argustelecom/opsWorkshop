package ru.argustelecom.box.nri.ports;

import org.junit.Test;
import ru.argustelecom.box.nri.ports.model.AccessTechnology;
import ru.argustelecom.box.nri.ports.model.OpticTransceiverFormFactor;
import ru.argustelecom.box.nri.ports.model.OpticTransceiverWaveLength;
import ru.argustelecom.box.nri.ports.model.PortOpticTransceiver;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;

import static org.junit.Assert.*;

public class PortOpticTransceiverDtoTranslatorTest {
	private PortOpticTransceiverDtoTranslator translator = new PortOpticTransceiverDtoTranslator();

	@Test
	public void shouldTranslateToDto(){
		PortOpticTransceiver port = new PortOpticTransceiver(1L) ;
		port.setAccessTechnology(AccessTechnology.xPON);
		port.setPortName("name");
		port.setPortNumber(1);
		port.setPortPurpose(PortPurpose.SUBSCRIBER);
		port.setResource(ResourceInstance.builder().id(1L).build());
		port.setTransmissionMedium(TransmissionMedium.COPPER);
		port.setTechnicalCondition(PortTechnicalCondition.IN_ORDER);

		port.setSerialNum("1213132");
		port.setWaveLength(OpticTransceiverWaveLength.BX);
		port.setFormFactor(OpticTransceiverFormFactor.SFP);
		port.setMacAddress("11:11:22:22:22:22");

		PortOpticTransceiverDto portDto = translator.translate(port);
		assertNotNull(portDto);
		assertEquals(port.getId(),portDto.getId());
		assertEquals(port.getPortName(),portDto.getPortName());
		assertEquals(port.getPortNumber(),portDto.getPortNumber());
		assertEquals(port.getAccessTechnology(),portDto.getAccessTechnology());
		assertEquals(port.getType(),portDto.getType());
		assertEquals(port.getPortPurpose(),portDto.getPortPurpose());
		assertEquals(port.getResource().getId(),portDto.getResourceId());
		assertEquals(port.getTechnicalCondition(),portDto.getTechnicalCondition());
		assertEquals(port.getTransmissionMedium(),portDto.getTransmissionMedium());


		assertEquals(port.getWaveLength(),portDto.getWaveLength());
		assertEquals(port.getFormFactor(),portDto.getFormFactor());
		assertEquals(port.getSerialNum(),portDto.getSerialNum());
		assertEquals(port.getMacAddress().getMacAddress(),portDto.getMacAddress());
	}

	@Test
	public void shouldValidateInput() throws Exception {
		assertNull(translator.translate(null));
	}

}