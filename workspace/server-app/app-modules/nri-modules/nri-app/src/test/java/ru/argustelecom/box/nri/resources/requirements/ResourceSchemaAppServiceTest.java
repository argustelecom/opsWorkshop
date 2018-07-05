package ru.argustelecom.box.nri.resources.requirements;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.nri.schema.ResourceSchemaRepository;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.service.ServiceSpecificationRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceSchemaAppServiceTest {

	@Mock
	private ServiceSpecificationRepository serviceSpecificationRepository;

	@Mock
	private ResourceSchemaRepository schemaRepository;

	@Mock
	private ResourceSchemaDtoTranslator resourceSchemaDtoTranslator;
	@InjectMocks
	private ResourceSchemaAppService service;


	@Test
	public void  shouldCreateResourceSchema() {
		ServiceSpec ss = ServiceSpec.builder().build();
		when(resourceSchemaDtoTranslator.translate(any())).thenReturn(ResourceSchemaDto.builder().id(1L).build());
		when(serviceSpecificationRepository.findOne(eq(1L))).thenReturn(ss);
		service.createResourceSchema("name",1L);
		verify(schemaRepository,times(1)).create(eq("name"),eq(ss));
	}
	@Test
	public void shouldRemoveResourceSchema(){
		service.removeResourceSchema(1L);
		verify(schemaRepository,times(1)).delete(eq(1L));
	}
	@Test
	public void shouldFindAll(){
		when(serviceSpecificationRepository.findOne(eq(1L))).thenReturn(ServiceSpec.builder().build());
		when(schemaRepository.findByServiceSpecification(any())).thenReturn(Collections.singletonList(ResourceSchema.builder().build()));
		when(resourceSchemaDtoTranslator.translate(any())).thenReturn(ResourceSchemaDto.builder().build());
		List<ResourceSchemaDto>  list = service.findAll(1L);
		assertNotNull(list);
		assertEquals(1,list.size());
	}
	@Test
	public void shouldChangeName(){
		ResourceSchema schema = ResourceSchema.builder().build();
		when(schemaRepository.findById(1L)).thenReturn(schema);
		service.changeName(ResourceSchemaDto.builder().id(1L).name("name").build());
		verify(schemaRepository,times(1)).save(eq(schema));
		assertEquals("name",schema.getName());
	}
	@Test
	public void shouldNotChangeName(){
		when(schemaRepository.findById(1L)).thenReturn(null);
		service.changeName(ResourceSchemaDto.builder().id(1L).name("name").build());
		verify(schemaRepository,times(0)).save(any());

	}
}