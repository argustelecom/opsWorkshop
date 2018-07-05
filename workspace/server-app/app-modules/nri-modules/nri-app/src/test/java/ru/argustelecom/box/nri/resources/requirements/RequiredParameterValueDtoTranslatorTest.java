package ru.argustelecom.box.nri.resources.requirements;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.schema.requirements.resources.comparators.CompareAction;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredParameterValue;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 19.09.2017.
 */
@RunWith(PowerMockRunner.class)
public class RequiredParameterValueDtoTranslatorTest {

	@Mock
	private ParameterSpecificationDtoTranslator specTranslator;

	@InjectMocks
	RequiredParameterValueDtoTranslator translator;

	@Test
	public void shouldReturnNull() throws Exception {
		assertNull(translator.translate(null));
	}

	@Test
	public void shouldTranslate() throws Exception {
		ParameterSpecification specification = ParameterSpecification.builder().id(2L).build();
		RequiredParameterValue value = RequiredParameterValue.builder()
				.id(1L)
				.compareAction(CompareAction.EQUALS)
				.parameterSpecification(specification)
				.requiredValue("111")
				.build();

		when(specTranslator.translate(anyObject())).thenReturn(ParameterSpecificationDto.builder().id(2L).build());

		RequiredParameterValueDto dto = translator.translate(value);

		assertNotNull(dto);
		assertEquals(new Long(1L), dto.getId());
		assertEquals(CompareAction.EQUALS, dto.getCompareAction());
		assertEquals("111", dto.getValue());
		assertNotNull(dto.getParameterSpecification());
		assertEquals(new Long(2L), dto.getParameterSpecification().getId());
	}
}