package ru.argustelecom.box.nri.resources.spec;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.spec.model.ParameterDataType;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 19.09.2017.
 */
@RunWith(PowerMockRunner.class)
public class ResourceSpecificationDtoTranslatorTest {

	@Mock
	private ParameterSpecificationDtoTranslator paramTranslator;

	@InjectMocks
	private ResourceSpecificationDtoTranslator translator;

	@Test
	public void shouldReturnNull() throws Exception {
		assertNull(translator.translate(null));
	}

	@Test
	public void shouldTranslate() throws Exception {
		ParameterSpecification paramSpecification = ParameterSpecification.builder()
				.id(1L)
				.dataType(ParameterDataType.INTEGER)
				.name("name")
				.regex(".*")
				.defaultValue("123")
				.required(true)
				.build();
		ResourceSpecification childSpecification = ResourceSpecification.builder()
				.id(2L)
				.name("name")
				.build();
		ResourceSpecification specification = ResourceSpecification.builder()
				.id(1L)
				.name("name")
				.isIndependent(true)
				.childSpecifications(Collections.singletonList(childSpecification))
				.parameters(Collections.singletonList(paramSpecification))
				.build();

		when(paramTranslator.translate(eq(paramSpecification)))
				.thenReturn(new ParameterSpecificationDtoTranslator().translate(paramSpecification));

		ResourceSpecificationDto dto = translator.translate(specification);

		assertNotNull(dto);
		assertEquals(new Long(1L),dto.getId());
		assertEquals("name",dto.getName());
		assertNotNull(dto.getChildSpecifications());
		assertFalse(dto.getChildSpecifications().isEmpty());
		assertNotNull(dto.getParameters());
		assertFalse(dto.getParameters().isEmpty());
		assertTrue(dto.getIsIndependent());

		verify(paramTranslator, times(1)).translate(eq(paramSpecification));
	}
}