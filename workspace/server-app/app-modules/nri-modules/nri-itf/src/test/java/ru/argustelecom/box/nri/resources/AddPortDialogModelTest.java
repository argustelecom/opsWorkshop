package ru.argustelecom.box.nri.resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.ContextMocker;
import ru.argustelecom.box.nri.ports.PortAppService;
import ru.argustelecom.box.nri.ports.PortEthernetDto;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;

import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by b.bazarov on 04.05.2018.
 */
@RunWith(PowerMockRunner.class)
public class AddPortDialogModelTest {
	@Mock
	private PortAppService portAppService;

	@InjectMocks
	private AddPortDialogModel model;

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPreRenderResourceNull() {
		model.preRender(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPreRenderSpecNull() {
		model.preRender(ResourceInstanceDto.builder().build());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPreRenderSpecEmptySupportedTypes() {
		model.preRender(ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().build()).build());
	}

	@Test
	public void shouldPreRenderOneType() {
		HashSet<PortType> ports = new HashSet<>();
		ports.add(PortType.ETHERNET_PORT);
		ResourceInstanceDto instance = ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().supportedPortTypes(ports).build()).build();
		model.preRender(instance);
		assertEquals(PortType.ETHERNET_PORT, model.getSelectedType());
		assertEquals(instance, model.getResource());
	}

	@Test
	public void shouldPreRender() {
		HashSet<PortType> ports = new HashSet<>();
		ports.add(PortType.ETHERNET_PORT);
		ports.add(PortType.OPTIC_TRANSCEIVER);
		ResourceInstanceDto instance = ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().supportedPortTypes(ports).build()).build();
		model.preRender(instance);
		assertNull(model.getSelectedType());
		assertEquals(instance, model.getResource());
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCreatePortPortTypeIsNull() {
		ContextMocker.mockFacesContext();
		HashSet<PortType> ports = new HashSet<>();
		ports.add(PortType.ETHERNET_PORT);
		ports.add(PortType.OPTIC_TRANSCEIVER);
		ResourceInstanceDto instance = ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().supportedPortTypes(ports).build()).build();
		model.preRender(instance);
		model.createPort();

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSavePortPortTypeIsNotInSupported() {
		ContextMocker.mockFacesContext();
		HashSet<PortType> ports = new HashSet<>();
		ports.add(PortType.ETHERNET_PORT);
		ResourceInstanceDto instance = ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().supportedPortTypes(ports).build()).build();
		model.preRender(instance);
		model.createPort();
		instance.getSpecification().getSupportedPortTypes().clear();
		model.save();

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSavePortTypeIsNull() {
		ContextMocker.mockFacesContext();
		HashSet<PortType> ports = new HashSet<>();
		ports.add(PortType.ETHERNET_PORT);
		ResourceInstanceDto instance = ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().supportedPortTypes(ports).build()).build();
		model.preRender(instance);
		model.save();

	}

	@Test
	public void shouldCreatePort() {
		ContextMocker.mockFacesContext();
		HashSet<PortType> ports = new HashSet<>();
		for (PortType type : PortType.values()) {
			ports.add(type);
		}
		ResourceInstanceDto instance = ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().supportedPortTypes(ports).build()).build();
		model.preRender(instance);

		for (PortType type : PortType.values()) {
			model.setSelectedType(type);
			model.createPort();
			assertEquals(type, model.getPort().getType());
		}
	}

	@Test
	public void shouldSavePort() {
		ContextMocker.mockFacesContext();
		HashSet<PortType> ports = new HashSet<>();
		for (PortType type : PortType.values()) {
			ports.add(type);
		}
		ResourceInstanceDto instance = ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().supportedPortTypes(ports).build()).build();


		for (PortType type : PortType.values()) {
			model.preRender(instance);
			model.setSelectedType(type);
			model.createPort();
			model.save();
		}
		verify(portAppService, times(1)).savePortCombo(any());
		verify(portAppService, times(1)).savePortPassive(any());
		verify(portAppService, times(1)).savePortOpticSplitter(any());
		verify(portAppService, times(1)).savePortOpticTransceiver(any());
		verify(portAppService, times(1)).savePortXDsl(any());
		verify(portAppService, times(1)).savePortEthernet(any());
		verify(portAppService, times(1)).savePortPon(any());
		assertEquals(7, PortType.values().length);
	}

	@Test
	public void shouldCleanParam() {
		ContextMocker.mockFacesContext();
		HashSet<PortType> ports = new HashSet<>();
		ports.add(PortType.OPTIC_TRANSCEIVER);

		ResourceInstanceDto instance = ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().supportedPortTypes(ports).build()).build();
		model.preRender(instance);
		model.createPort();

		model.cleanCreationParams();
		assertNull(model.getResource());
		assertNull(model.getSelectedType());
		assertNull(model.getPort());
	}

	@Test
	public void shouldCreateFivePorts() {
		ContextMocker.mockFacesContext();
		HashSet<PortType> ports = new HashSet<>();
		for (PortType type : PortType.values()) {
			ports.add(type);
		}
		ResourceInstanceDto instance = ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().supportedPortTypes(ports).build()).build();
		model.preRender(instance);

		model.setNewPortsNumber(5);

		model.setSelectedType(PortType.ETHERNET_PORT);
		model.createPort();
		model.save();

		verify(portAppService, times(5)).savePortEthernet(any());
	}
	@Test
	public void shouldAddIncrementToName(){
		ContextMocker.mockFacesContext();
		HashSet<PortType> ports = new HashSet<>();
		for (PortType type : PortType.values()) {
			ports.add(type);
		}
		ResourceInstanceDto instance = ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().supportedPortTypes(ports).build()).build();
		model.preRender(instance);

		model.setNewPortsNumber(5);
		model.setShouldIncrementName(true);

		model.setSelectedType(PortType.ETHERNET_PORT);
		model.createPort();
		model.getPort().setPortName("port ");
		model.getPort().setPortNumber(1);
		model.save();

		ArgumentCaptor<PortEthernetDto> filledPort = ArgumentCaptor.forClass(PortEthernetDto.class);
		verify(portAppService,times(5)).savePortEthernet(filledPort.capture());
		assertEquals("port 1",filledPort.getAllValues().get(0).getPortName());
		assertEquals("port 5",filledPort.getAllValues().get(4).getPortName());
	}
	@Test
	public void shouldNotAddIncrementToName(){
		ContextMocker.mockFacesContext();
		HashSet<PortType> ports = new HashSet<>();
		for (PortType type : PortType.values()) {
			ports.add(type);
		}
		ResourceInstanceDto instance = ResourceInstanceDto.builder().specification(ResourceSpecificationDto.builder().supportedPortTypes(ports).build()).build();
		model.preRender(instance);

		model.setNewPortsNumber(5);
		model.setShouldIncrementName(false);

		model.setSelectedType(PortType.ETHERNET_PORT);
		model.createPort();
		model.getPort().setPortName("port");
		model.getPort().setPortNumber(1);
		model.save();

		ArgumentCaptor<PortEthernetDto> filledPort = ArgumentCaptor.forClass(PortEthernetDto.class);
		verify(portAppService,times(5)).savePortEthernet(filledPort.capture());
		assertEquals("port",filledPort.getAllValues().get(0).getPortName());
		assertEquals("port",filledPort.getAllValues().get(4).getPortName());
	}
}