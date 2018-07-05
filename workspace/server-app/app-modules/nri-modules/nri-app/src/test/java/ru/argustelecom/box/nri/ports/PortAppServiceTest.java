package ru.argustelecom.box.nri.ports;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.nri.ports.model.AccessTechnology;
import ru.argustelecom.box.nri.ports.model.ComboPortUsageType;
import ru.argustelecom.box.nri.ports.model.EthernetPortType;
import ru.argustelecom.box.nri.ports.model.OpticSplitterRole;
import ru.argustelecom.box.nri.ports.model.OpticTransceiverFormFactor;
import ru.argustelecom.box.nri.ports.model.OpticTransceiverWaveLength;
import ru.argustelecom.box.nri.ports.model.PassivePortType;
import ru.argustelecom.box.nri.ports.model.PonConnectorType;
import ru.argustelecom.box.nri.ports.model.Port;
import ru.argustelecom.box.nri.ports.model.PortCombo;
import ru.argustelecom.box.nri.ports.model.PortEthernet;
import ru.argustelecom.box.nri.ports.model.PortOpticSplitter;
import ru.argustelecom.box.nri.ports.model.PortOpticTransceiver;
import ru.argustelecom.box.nri.ports.model.PortPassive;
import ru.argustelecom.box.nri.ports.model.PortPon;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.ports.model.PortXDsl;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;
import ru.argustelecom.box.nri.ports.model.XDslPortType;
import ru.argustelecom.box.nri.resources.ResourceInstanceRepository;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by b.bazarov on 04.05.2018.
 */
@RunWith(PowerMockRunner.class)
public class PortAppServiceTest {

	@Mock
	private PortRepository portRepository;

	@Mock
	private PortDtoTranslator portDtoTranslator;

	@Mock
	private PortEthernetDtoTranslator portEthernetDtoTranslator;
	@Mock
	private PortXDslDtoTranslator portXDslDtoTranslator;
	@Mock
	private PortOpticSplitterDtoTranslator portOpticSplitterDtoTranslator;
	@Mock
	private PortPonDtoTranslator portPonDtoTranslator;
	@Mock
	private PortComboDtoTranslator portComboDtoTranslator;
	@Mock
	private PortPassiveDtoTranslator portPassiveDtoTranslator;
	@Mock
	private PortOpticTransceiverDtoTranslator portPortOpticTransceiverDtoTranslator;
	@Mock
	private IdSequenceService idSequenceService;

	@Mock
	private ResourceInstanceRepository resourceInstanceRepository;
	@InjectMocks
	private PortAppService testingService;

	private List<Port> ports = Arrays.asList(new PortEthernet(1L), new PortOpticSplitter(2L),
			new PortCombo(3L), new PortXDsl(4L), new PortPon(5L), new PortPassive(6L), new PortOpticTransceiver(7L));

	@Before
	public void init(){
		when(idSequenceService.nextValue(any())).thenReturn(1L);
	}

	@Test
	public void shouldLoadAllPortsByResource() {
		ResourceInstanceDto resourceInstanceDto = ResourceInstanceDto.builder().id(1L).build();
		when(portRepository.loadAllPortsByResource(eq(resourceInstanceDto.getId()))).thenReturn(ports);
		testingService.loadAllPortsByResource(resourceInstanceDto);
		verify(portDtoTranslator, times(7)).translate(any());
	}

	@Test
	public void shouldLoadEmptyListByResource() {
		List<PortDto> list = testingService.loadAllPortsByResource(null);
		assertNotNull(list);
		assertTrue(list.size() == 0);
	}

	@Test
	public void shouldLoadPort() {

		for (int i = 0; i < PortType.values().length; i++) {
			when(portRepository.findById(1L)).thenReturn(ports.get(i));
			testingService.loadPort(1L);
		}
		List<DefaultDtoTranslator> translators = Arrays.asList(portEthernetDtoTranslator, portXDslDtoTranslator, portOpticSplitterDtoTranslator,
				portPonDtoTranslator, portComboDtoTranslator, portPassiveDtoTranslator, portPortOpticTransceiverDtoTranslator);
		for (int i = 0 ; i <  PortType.values().length;i++ ) {
			verify(translators.get(i), times(1)).translate(any());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateInputSavePortEthernetNullPort(){
		testingService.savePortEthernet(null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateInputSavePortEthernetResource(){
		testingService.savePortEthernet(PortEthernetDto.builder().build());
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotSavePortEthernetCouldNotFindResource(){
		when(resourceInstanceRepository.findOne(1L)).thenReturn(null);
		testingService.savePortEthernet(PortEthernetDto.builder().resourceId(1L).build());
	}
	@Test(expected = IllegalStateException.class)
	public void shouldNotSavePortEthernetCouldNotFindPort(){
		when(resourceInstanceRepository.findOne(1L)).thenReturn(ResourceInstance.builder().build());
		when(portRepository.findById(1L)).thenReturn(null);
		testingService.savePortEthernet(PortEthernetDto.builder().id(1L).resourceId(1L).build());
	}
	@Test
	public void shouldSaveNewPortEthernet(){
		when(resourceInstanceRepository.findOne(1L)).thenReturn(ResourceInstance.builder().build());
		PortEthernetDto dto = PortEthernetDto.builder().resourceId(1L).build();
		//Заполняем специфические параметры
		dto.setMacAddress("11:11:11:AA:bb:cc");
		dto.setPortType(EthernetPortType.PORT_TYPE_100FE);
		testingService.savePortEthernet(dto);
		ArgumentCaptor<PortEthernet> filledPort = ArgumentCaptor.forClass(PortEthernet.class);
		verify(portRepository).save(filledPort.capture());
		assertEquals("11:11:11:AA:bb:cc",filledPort.getValue().getMacAddress().getMacAddress());
		assertEquals(EthernetPortType.PORT_TYPE_100FE,filledPort.getValue().getPortType());
	}

	@Test
	public void shouldSaveNewPortXDsl(){
		when(resourceInstanceRepository.findOne(1L)).thenReturn(ResourceInstance.builder().build());
		PortXDslDto dto = PortXDslDto.builder().resourceId(1L).build();
		//Заполняем специфические параметры
		dto.setMacAddress("11:11:11:AA:bb:cc");
		dto.setPortType(XDslPortType.ADSL2_PLUS);
		testingService.savePortXDsl(dto);
		ArgumentCaptor<PortXDsl> filledPort = ArgumentCaptor.forClass(PortXDsl.class);
		verify(portRepository).save(filledPort.capture());
		assertEquals(dto.getMacAddress(),filledPort.getValue().getMacAddress().getMacAddress());
		assertEquals(dto.getPortType(),filledPort.getValue().getPortType());
	}

	@Test
	public void shouldSaveNewOpticSplitter(){
		when(resourceInstanceRepository.findOne(1L)).thenReturn(ResourceInstance.builder().build());
		PortOpticSplitterDto dto = PortOpticSplitterDto.builder().resourceId(1L).build();
		//Заполняем специфические параметры
		dto.setConnectorType(PonConnectorType.FC);
		dto.setRole(OpticSplitterRole.INCOMING);
		testingService.savePortOpticSplitter(dto);
		ArgumentCaptor<PortOpticSplitter> filledPort = ArgumentCaptor.forClass(PortOpticSplitter.class);
		verify(portRepository).save(filledPort.capture());
		assertEquals(dto.getConnectorType(),filledPort.getValue().getConnectorType());
		assertEquals(dto.getRole(),filledPort.getValue().getRole());
	}

	@Test
	public void shouldSaveNewPortCombo(){
		when(resourceInstanceRepository.findOne(1L)).thenReturn(ResourceInstance.builder().build());
		PortComboDto dto = PortComboDto.builder().resourceId(1L).build();
		//Заполняем специфические параметры
		dto.setMacAddress("11:11:11:AA:bb:cc");
		dto.setUsageType(ComboPortUsageType.ETHERNET_PORT);
		dto.setPortType(EthernetPortType.PORT_TYPE_100FE);
		testingService.savePortCombo(dto);
		ArgumentCaptor<PortCombo> filledPort = ArgumentCaptor.forClass(PortCombo.class);
		verify(portRepository).save(filledPort.capture());
		assertEquals(dto.getMacAddress(),filledPort.getValue().getMacAddress().getMacAddress());
		assertEquals(dto.getPortType(),filledPort.getValue().getPortType());
		assertEquals(dto.getUsageType(),filledPort.getValue().getUsageType());
	}
	@Test
	public void shouldSaveNewPortPon(){
		when(resourceInstanceRepository.findOne(1L)).thenReturn(ResourceInstance.builder().build());
		PortPonDto dto = PortPonDto.builder().resourceId(1L).build();
		//Заполняем специфические параметры
		dto.setConnectorType(PonConnectorType.FC);
		dto.setMaxSubscriberNum(64);
		testingService.savePortPon(dto);
		ArgumentCaptor<PortPon> filledPort = ArgumentCaptor.forClass(PortPon.class);
		verify(portRepository).save(filledPort.capture());
		assertEquals(dto.getConnectorType(),filledPort.getValue().getConnectorType());
		assertEquals(dto.getMaxSubscriberNum(),filledPort.getValue().getMaxSubscriberNum());
	}

	@Test
	public void shouldSaveNewPortPassive(){
		when(resourceInstanceRepository.findOne(1L)).thenReturn(ResourceInstance.builder().build());
		PortPassiveDto dto = PortPassiveDto.builder().resourceId(1L).build();
		//Заполняем специфические параметры
		dto.setPortType(PassivePortType.FC);
		testingService.savePortPassive(dto);
		ArgumentCaptor<PortPassive> filledPort = ArgumentCaptor.forClass(PortPassive.class);
		verify(portRepository).save(filledPort.capture());
		assertEquals(dto.getPortType(),filledPort.getValue().getPortType());
	}

	@Test
	public void shouldSaveNewPortOpticTransceiver(){
		when(resourceInstanceRepository.findOne(1L)).thenReturn(ResourceInstance.builder().build());
		PortOpticTransceiverDto dto = PortOpticTransceiverDto.builder().resourceId(1L).build();
		//Заполняем специфические параметры
		dto.setMacAddress("11:11:11:AA:bb:cc");
		dto.setFormFactor(OpticTransceiverFormFactor.SFP);
		dto.setWaveLength(OpticTransceiverWaveLength.BX);
		dto.setSerialNum("21");

		testingService.savePortOpticTransceiver(dto);
		ArgumentCaptor<PortOpticTransceiver> filledPort = ArgumentCaptor.forClass(PortOpticTransceiver.class);
		verify(portRepository).save(filledPort.capture());
		assertEquals(dto.getMacAddress(),filledPort.getValue().getMacAddress().getMacAddress());
		assertEquals(dto.getFormFactor(),filledPort.getValue().getFormFactor());
		assertEquals(dto.getWaveLength(),filledPort.getValue().getWaveLength());
		assertEquals(dto.getSerialNum(),filledPort.getValue().getSerialNum());
	}
	@Test
	public void shouldSavePortOpticTransceiver(){
		when(resourceInstanceRepository.findOne(1L)).thenReturn(ResourceInstance.builder().build());

		PortPassiveDto dto = PortPassiveDto.builder().id(1L).resourceId(1L).build();
		//Заполняем специфические параметры
		PortPassive port = new PortPassive(2L);
		port.setPortNumber(3);
		port.setPortName("name1");
		port.setAccessTechnology(AccessTechnology.FTTx);
		port.setPortPurpose(PortPurpose.SUBSCRIBER);
		port.setTechnicalCondition(PortTechnicalCondition.IN_ORDER);
		port.setTransmissionMedium(TransmissionMedium.COPPER);
		port.setPortType(PassivePortType.RJ45);
		when(portRepository.findById(1L)).thenReturn(port);
		dto.setPortNumber(2);
		dto.setPortName("name2");
		dto.setAccessTechnology(AccessTechnology.FTTx);
		dto.setPortPurpose(PortPurpose.SUBSCRIBER);
		dto.setTechnicalCondition(PortTechnicalCondition.IN_ORDER);
		dto.setTransmissionMedium(TransmissionMedium.COPPER);
		dto.setPortType(PassivePortType.RJ45);


		testingService.savePortPassive(dto);
		ArgumentCaptor<PortPassive> filledPort = ArgumentCaptor.forClass(PortPassive.class);
		verify(portRepository).save(filledPort.capture());
		assertEquals(dto.getPortNumber(),filledPort.getValue().getPortNumber());
		assertEquals(dto.getPortName(),filledPort.getValue().getPortName());
		assertEquals(dto.getAccessTechnology(),filledPort.getValue().getAccessTechnology());
		assertEquals(dto.getPortPurpose(),filledPort.getValue().getPortPurpose());
		assertEquals(dto.getTechnicalCondition(),filledPort.getValue().getTechnicalCondition());
		assertEquals(dto.getTransmissionMedium(),filledPort.getValue().getTransmissionMedium());
		assertEquals(dto.getPortType(),filledPort.getValue().getPortType());
	}

	@Test
	public void shouldDelete(){
		testingService.deletePort(1L);
		verify(portRepository,times(1)).deletePort(eq(1L));
	}

}