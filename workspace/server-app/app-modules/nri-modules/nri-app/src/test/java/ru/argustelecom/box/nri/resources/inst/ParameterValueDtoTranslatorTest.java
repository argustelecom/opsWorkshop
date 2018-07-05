package ru.argustelecom.box.nri.resources.inst;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.nri.resources.model.ParameterValue;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author a.wisniewski
 * @since 20.09.2017
 */
@RunWith(MockitoJUnitRunner.class)
public class ParameterValueDtoTranslatorTest {

	@Mock
	private ParameterSpecificationDtoTranslator specTranslator;

	@InjectMocks
	private ParameterValueDtoTranslator paramValueTranslator;

	@Test
	public void translate_null() throws Exception {
		assertNull(paramValueTranslator.translate(null));
	}

	@Test
	public void translate() throws Exception {
		ParameterSpecification specification = ParameterSpecification.builder().build();
		ParameterSpecificationDto specificationDto = ParameterSpecificationDto.builder().build();
		when(specTranslator.translate(specification)).thenReturn(specificationDto);

		ParameterValue param = ParameterValue.builder()
				.id(1L)
				.specification(specification)
				.value("value").build();
		ParameterValueDto paramDto = paramValueTranslator.translate(param);
		assertEquals((Long)1L, paramDto.getId());
		assertEquals(specificationDto, paramDto.getSpecification());
		assertEquals("value", paramDto.getValue());
	}

}