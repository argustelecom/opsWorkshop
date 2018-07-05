package ru.argustelecom.box.nri.ports;

import org.junit.Test;
import ru.argustelecom.box.nri.ports.model.AccessTechnology;
import ru.argustelecom.box.nri.ports.model.OpticSplitterRole;
import ru.argustelecom.box.nri.ports.model.PonConnectorType;
import ru.argustelecom.box.nri.ports.model.PortOpticSplitter;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;

import static org.junit.Assert.*;

public class PortOpticSplitterDtoTranslatorTest {

	private PortOpticSplitterDtoTranslator translator = new PortOpticSplitterDtoTranslator();

	@Test
	public void shouldTranslateToDto(){
		PortOpticSplitter port = new PortOpticSplitter(1L) ;
		port.setAccessTechnology(AccessTechnology.xPON);
		port.setPortName("name");
		port.setPortNumber(1);
		port.setPortPurpose(PortPurpose.SUBSCRIBER);
		port.setResource(ResourceInstance.builder().id(1L).build());
		port.setTransmissionMedium(TransmissionMedium.COPPER);
		port.setTechnicalCondition(PortTechnicalCondition.IN_ORDER);


		port.setRole(OpticSplitterRole.INCOMING);
		port.setConnectorType(PonConnectorType.FC);


		PortOpticSplitterDto portDto = translator.translate(port);
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

		assertEquals(port.getRole(),portDto.getRole());
		assertEquals(port.getConnectorType(),portDto.getConnectorType());
	}

	@Test
	public void shouldValidateInput() throws Exception {
		assertNull(translator.translate(null));
	}
}