package ru.argustelecom.box.nri.resources.requirements;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredParameterValue;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author b.bazarov
 * @since 24.10.2017
 */
@RunWith(MockitoJUnitRunner.class)
public class RequiredItemDtoTranslatorTest {


	@Mock
	private ResourceSpecificationDtoTranslator specTranslator;

	@Mock
	private RequiredParameterValueDtoTranslator paramTraslator;


	@InjectMocks
	private RequiredItemDtoTranslator translator;

	@Test
	public void translate_null() throws Exception {
		assertNull(translator.translate(null));
	}
	@Test
	public void translateEmptyParam(){
		RequiredItem item = RequiredItem.builder().build();
		RequiredItemDto dto = translator.translate(item);
		assertNotNull(dto);
		assertNotNull(dto.getChildren());
		assertEquals(0,dto.getChildren().size());
		assertNull(dto.getResourceSpecification());
		assertNull(dto.getId());
	}
	@Test
	public void translateWithChildren(){
		ResourceSpecification specification = ResourceSpecification.builder().build();
		ResourceSpecificationDto specificationDto = ResourceSpecificationDto.builder().build();

		RequiredParameterValue value =  RequiredParameterValue.builder().build();
		RequiredParameterValueDto valueDto =  RequiredParameterValueDto.builder().build();


		when(specTranslator.translate(specification)).thenReturn(specificationDto);
		when(paramTraslator.translate(value)).thenReturn(valueDto);


		RequiredItem item = RequiredItem.builder().id(1L)
				.resourceSpecification(specification).requiredParameters(Arrays.asList(value))
				.children(Arrays.asList(RequiredItem.builder().id(2L)
						.resourceSpecification(specification).requiredParameters(Arrays.asList(value)).build() )).build();

		RequiredItemDto dto = translator.translate(item);

		assertNotNull(dto);
		assertEquals(specificationDto,dto.getResourceSpecification());
		assertEquals(new Long(1L),dto.getId());
		assertNotNull(dto.getRequiredParameters());
		assertEquals(1,dto.getRequiredParameters().size());
		assertEquals(valueDto,dto.getRequiredParameters().get(0));
		assertNotNull(dto.getChildren());
		assertEquals(1,dto.getChildren().size());


	}

}