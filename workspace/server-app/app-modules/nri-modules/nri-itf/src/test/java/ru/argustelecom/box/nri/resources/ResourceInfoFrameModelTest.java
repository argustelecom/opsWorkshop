package ru.argustelecom.box.nri.resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.inst.ParameterValueDto;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceAppService;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.model.ResourceStatus;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author d.khekk
 * @since 25.09.2017
 */
@RunWith(PowerMockRunner.class)
public class ResourceInfoFrameModelTest {

	@Mock
	private ResourceInstanceAppService service;

	@InjectMocks
	private ResourceInfoFrameModel frameModel;

	@Test
	public void shouldInitExistingResource() throws Exception {
		ResourceSpecificationDto specificationDto = ResourceSpecificationDto.builder().build();
		ParameterSpecificationDto paramSpec1 = ParameterSpecificationDto.builder().id(1L).build();
		ParameterSpecificationDto paramSpec2 = ParameterSpecificationDto.builder().id(2L).build();
		ParameterSpecificationDto paramSpec3 = ParameterSpecificationDto.builder().id(3L).build();
		ParameterValueDto parameterDto = ParameterValueDto.builder().id(1L).specification(paramSpec1).build();
		ParameterValueDto parameterDto2 = ParameterValueDto.builder().id(2L).specification(paramSpec2).build();
		ParameterValueDto parameterDto3 = ParameterValueDto.builder().id(13L).specification(paramSpec3).build();
		ResourceInstanceDto resourceDto = ResourceInstanceDto.builder()
				.id(1L)
				.specification(specificationDto)
				.parameterValues(Arrays.asList(parameterDto2, parameterDto, parameterDto3)).build();
		frameModel.preRender(resourceDto);

		assertEquals(resourceDto, frameModel.getResource());
		assertEquals(specificationDto, frameModel.getSpecification());
		assertTrue(frameModel.getParameters().contains(parameterDto));
		assertEquals(Arrays.asList(parameterDto, parameterDto2, parameterDto3), frameModel.getParameters());
	}

	@Test
	public void shouldCallRename() throws Exception {
		doNothing().when(service).renameResource(any());

		frameModel.changeName();
		verify(service, times(1)).renameResource(any());
	}

	@Test
	public void shouldCallChangeParameter() throws Exception {
		doNothing().when(service).changeParameter(any(), any());

		frameModel.changeParameter(ParameterValueDto.builder().build());
		verify(service, times(1)).changeParameter(any(), any());
	}
	@Test
	public void shouldChangeStatus(){
		doNothing().when(service).changeStatus(any());

		frameModel.changeStatus();
		verify(service, times(1)).changeStatus(any());
	}
	@Test
	public void shouldReturnListOfAllStatuses(){
		List<ResourceStatus> rs = new ArrayList<>(Arrays.asList(frameModel.getAllStatuses()));
		assertEquals(4,rs.size());
		assertTrue(rs.contains(ResourceStatus.UNKNOWN));
		assertTrue(rs.contains(ResourceStatus.ACTIVE));
		assertTrue(rs.contains(ResourceStatus.DISABLED));
		assertTrue(rs.contains(ResourceStatus.RESERVED));

	}

//	@Test
//	public void shouldCreateNewResourceWithoutParent() throws Exception {
//		when(service.createResource(any(), any())).then(invocation -> invocation.getArgumentAt(0, ResourceInstanceDto.class));
//
//		ResourceSpecificationDto specificationDto = ResourceSpecificationDto.builder().build();
//		ParameterValueDto parameterDto = ParameterValueDto.builder().build();
//		List<ParameterValueDto> parameters = new ArrayList<>();
//		parameters.add(parameterDto);
//		ResourceInstanceDto resourceDto = ResourceInstanceDto.builder()
//				.id(1L)
//				.specification(specificationDto)
//				.parameterValues(parameters).build();
//		frameModel.preRender(resourceDto);
//
//		frameModel.createResource();
//
//		assertNotNull(frameModel.getResource());
//		assertEquals(specificationDto, frameModel.getResource().getSpecification());
//		assertFalse(frameModel.getCreationMode());
//	}
}