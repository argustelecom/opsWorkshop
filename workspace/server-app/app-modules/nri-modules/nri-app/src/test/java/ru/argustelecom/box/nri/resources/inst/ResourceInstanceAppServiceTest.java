package ru.argustelecom.box.nri.resources.inst;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.nri.resources.ResourceInstanceRepository;
import ru.argustelecom.box.nri.resources.model.ParameterValue;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.model.ResourceStatus;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationRepository;
import ru.argustelecom.box.nri.resources.spec.model.ParameterDataType;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author d.khekk
 * @since 29.09.2017
 */
@RunWith(PowerMockRunner.class)
public class ResourceInstanceAppServiceTest {

	@Mock
	private IdSequenceService idService;

	@Mock
	private ResourceSpecificationRepository specificationRepository;

	@Mock
	private ResourceInstanceDtoTranslator translator;

	@Mock
	private ResourceInstanceRepository repository;

	@InjectMocks
	private ResourceInstanceAppService service;

	@Before
	public void setUp() throws Exception {
		when(repository.findOne(anyLong())).thenReturn(ResourceInstance.builder()
				.id(1L)
				.name("name")
				.status(ResourceStatus.ACTIVE)
				.parameterValues(Collections.singletonList(ParameterValue.builder().id(1L)
						.specification(ParameterSpecification.builder()
								.id(1L)
								.dataType(ParameterDataType.STRING)
								.build())
						.value("ASD")
						.build()))
				.build());
		doNothing().when(repository).save(any());
		when(translator.translate(any())).then(invocation -> {
			ResourceInstance res = invocation.getArgumentAt(0, ResourceInstance.class);
			return ResourceInstanceDto.builder().id(res.getId()).name(res.getName()).build();
		});
	}

	@Test
	public void shouldFindResource() throws Exception {
		ResourceInstanceDto resource = service.findResource(1L);
		assertNotNull(resource);
		assertEquals(new Long(1), resource.getId());
	}

	@Test
	public void shouldChangeStatus() throws Exception {
		ResourceInstanceDto resource = ResourceInstanceDto.builder().id(1L).name("name").status(ResourceStatus.DISABLED).build();
		service.changeStatus(resource);

		verify(repository, atLeastOnce()).save(any());
		verify(repository, atLeastOnce()).findOne(any());
	}

	@Test
	public void shouldDeleteResource() throws Exception {
		doNothing().when(repository).delete(anyLong());

		ResourceInstanceDto resource = ResourceInstanceDto.builder().id(1L).name("name").status(ResourceStatus.DISABLED).build();
		service.removeResource(resource.getId());

		verify(repository, atLeastOnce()).delete(anyLong());
	}

	@Test
	public void shouldRename() throws Exception {
		ResourceInstanceDto resource = ResourceInstanceDto.builder().id(1L).name("name1").status(ResourceStatus.DISABLED).build();
		service.renameResource(resource);

		verify(repository, atLeastOnce()).save(any());
		verify(repository, atLeastOnce()).findOne(any());
	}

	@Test
	public void shouldChangeParameter() throws Exception {
		ResourceInstanceDto resource = ResourceInstanceDto.builder().id(1L).name("name1").status(ResourceStatus.DISABLED).build();
		ParameterValueDto parameterValueDto = ParameterValueDto.builder()
				.id(1L)
				.specification(ParameterSpecificationDto.builder()
						.id(1L)
						.dataType(ParameterDataType.STRING)
						.build())
				.value("QWE")
				.build();
		service.changeParameter(resource, parameterValueDto);

		verify(repository, atLeastOnce()).save(any());
		verify(repository, atLeastOnce()).findOne(any());
	}

	@Test
	public void createResourceWithoutParent() throws Exception {
		when(repository.create(any())).then(invocation -> {
			ResourceInstance res = invocation.getArgumentAt(0, ResourceInstance.class);
			return ResourceInstance.builder().id(1L).status(res.getStatus()).name(res.getName()).build();
		});
		when(idService.nextValue(any())).thenReturn(1L);

		ParameterSpecificationDto paramSpecDto = ParameterSpecificationDto.builder()
				.id(1L)
				.dataType(ParameterDataType.STRING)
				.build();
		ResourceSpecification resSpec = ResourceSpecification.builder()
				.id(1L)
				.parameters(Collections.singletonList(ParameterSpecification.builder()
						.id(1L)
						.dataType(ParameterDataType.STRING)
						.build()))
				.build();
		when(specificationRepository.findOne(anyLong())).thenReturn(resSpec);

		ResourceInstanceDto resourceBefore = ResourceInstanceDto.builder()
				.name("name")
				.status(ResourceStatus.ACTIVE)
				.specification(ResourceSpecificationDto.builder()
						.id(1L)
						.parameters(Collections.singletonList(paramSpecDto))
						.build())
				.parameterValues(Collections.singletonList(ParameterValueDto.builder()
						.value("123")
						.specification(paramSpecDto)
						.build()))
				.build();
		ResourceInstanceDto resourceAfter = service.createResource(resourceBefore, null);

		assertNotNull(resourceAfter);
		assertNull(resourceBefore.getId());
		assertNotNull(resourceAfter.getId());
	}

	@Test
	public void createResourceWithParent() throws Exception {
		when(repository.create(any())).then(invocation -> {
			ResourceInstance res = invocation.getArgumentAt(0, ResourceInstance.class);
			return ResourceInstance.builder().id(1L).status(res.getStatus()).name(res.getName()).build();
		});
		when(specificationRepository.findOne(anyLong())).thenReturn(ResourceSpecification.builder().build());
		when(idService.nextValue(any())).thenReturn(1L);

		ResourceInstanceDto resourceBefore = ResourceInstanceDto.builder().name("name").status(ResourceStatus.ACTIVE).specification(ResourceSpecificationDto.builder().id(1L).build()).build();
		ResourceInstanceDto parentResource = ResourceInstanceDto.builder().id(2L).name("parent resource").build();
		ResourceInstanceDto resourceAfter = service.createResource(resourceBefore, parentResource);

		assertNotNull(resourceAfter);
		assertNull(resourceBefore.getId());
		assertNotNull(resourceAfter.getId());
	}
}