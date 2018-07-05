package ru.argustelecom.box.nri.resources.spec;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.spec.model.ParameterDataType;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;

import static org.junit.Assert.*;

/**
 * Created by s.kolyada on 19.09.2017.
 */
@RunWith(PowerMockRunner.class)
public class ParameterSpecificationDtoTranslatorTest {

	@InjectMocks
	private ParameterSpecificationDtoTranslator translator;

	@Test
	public void shouldReturnNull() throws Exception {
		assertNull(translator.translate(null));
	}

	@Test
	public void shouldTranslate() throws Exception {
		ParameterSpecification specification = ParameterSpecification.builder()
				.id(1L)
				.dataType(ParameterDataType.INTEGER)
				.name("name")
				.regex(".*")
				.defaultValue("123")
				.required(true)
				.build();

		ParameterSpecificationDto dto = translator.translate(specification);

		assertNotNull(dto);
		assertEquals(new Long(1L),dto.getId());
		assertEquals("name",dto.getName());
		assertEquals(".*",dto.getRegex());
		assertEquals("123",dto.getDefaultValue());
		assertEquals(ParameterDataType.INTEGER,dto.getDataType());
		assertTrue(dto.getRequired());
	}
}