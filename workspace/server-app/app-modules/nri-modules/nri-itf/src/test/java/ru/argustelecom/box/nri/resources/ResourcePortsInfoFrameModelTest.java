package ru.argustelecom.box.nri.resources;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.ports.PortAppService;
import ru.argustelecom.box.nri.ports.PortDto;
import ru.argustelecom.box.nri.ports.PortEthernetDto;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;


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
public class ResourcePortsInfoFrameModelTest {

	@Mock
	private PortAppService portAppService;

	@InjectMocks
	private ResourcePortsInfoFrameModel model;

	private List<PortDto> list = Arrays.asList(PortEthernetDto.builder().id(1L).build());

	@Test
	public void shouldPrerender() {
		ResourceInstanceDto resource = ResourceInstanceDto.builder().id(1L).build();
		when(portAppService.loadAllPortsByResource(eq(resource))).thenReturn(list);
		model.preRender(resource);
		assertEquals(model.getPortList(), list);
		assertEquals(model.getResource(), resource);
	}

	@Test
	public void shouldNotDelete() {
		model.deletePort();
		verify(portAppService, times(0)).deletePort(any());
	}

	@Test
	public void shouldNotDeleteNullId() {
		PortEthernetDto port = PortEthernetDto.builder().build();
		model.setSelectedPort(port);
		model.deletePort();
		verify(portAppService, times(0)).deletePort(any());
	}

	@Test
	@Ignore("http://gitlab/Box/Box/-/jobs/29271")
	public void shouldDelete() {

		PortEthernetDto port = PortEthernetDto.builder().id(1L).build();
		model.setSelectedPort(port);
		model.deletePort();
		assertEquals(model.getSelectedPort(),port);
		verify(portAppService, times(1)).deletePort(eq(port.getId()));
	}

}