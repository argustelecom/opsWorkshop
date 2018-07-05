package ru.argustelecom.box.nri.resources.requirements;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by b.bazarov on 19.09.2017.
 */
@RunWith(PowerMockRunner.class)
public class ResourceSchemaDtoTranslatorTest {

	@Mock
	private RequiredItemDtoTranslator requiredItemDtoTranslator;

	@InjectMocks
	private ResourceSchemaDtoTranslator  resourceSchemaDtoTranslator;

	@Test
	public void shouldReturnNull() throws Exception {
		assertNull(resourceSchemaDtoTranslator.translate(null));
	}
	@Test
	public void shouldTranslate(){
		RequiredItem ri = RequiredItem.builder().build();
		RequiredItemDto riDto = RequiredItemDto.builder().build();

		when(requiredItemDtoTranslator.translate(ri)).thenReturn(riDto);
		ResourceSchema businessObject = ResourceSchema.builder().id(1L)
				.requirements(Arrays.asList(ri))
				.name("name")
				.build();
		ResourceSchemaDto dto = resourceSchemaDtoTranslator.translate(businessObject);
		assertNotNull(dto);
		assertEquals("name",dto.getName());
		assertEquals(new Long(1L),dto.getId());
		assertNotNull(dto.getRequirements());
		assertEquals(1,dto.getRequirements().size());
		assertEquals(riDto,dto.getRequirements().get(0));
	}

	@Test
	public void shouldTranslateEmptyRequirements(){
		ResourceSchema businessObject = ResourceSchema.builder().id(1L)
				.name("name")
				.build();

		ResourceSchemaDto dto = resourceSchemaDtoTranslator.translate(businessObject);

		assertNotNull(dto);
		assertNotNull(dto.getRequirements());
		assertEquals(0,dto.getRequirements().size());
	}

}