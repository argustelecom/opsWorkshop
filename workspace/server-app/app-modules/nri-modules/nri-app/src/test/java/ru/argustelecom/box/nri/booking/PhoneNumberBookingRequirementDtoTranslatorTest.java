package ru.argustelecom.box.nri.booking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDto;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDtoTranslator;
import ru.argustelecom.box.nri.schema.requirements.phone.model.PhoneNumberBookingRequirement;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;


import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by b.bazarov on 01.02.2018
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberBookingRequirementDtoTranslatorTest {

	@InjectMocks
	private PhoneNumberBookingRequirementDtoTranslator phoneNumberBookingRequirementDtoTranslator;

	@Mock
	private ResourceSchemaDtoTranslator schemaDtoTranslator;

	private PhoneNumberBookingRequirement requirement = PhoneNumberBookingRequirement.builder().id(1L).name("name")
			.build();
	private ResourceSchema schema = ResourceSchema.builder().name("schema").id(1L).build();

	private ResourceSchemaDto schemaDto = ResourceSchemaDto.builder().id(2L).build();

	@Before
	public void setUp() throws Exception {
		requirement.setBookSchema(schema);
	}


	@Test
	public void shouldValidateInput() throws Exception {
		assertNull(phoneNumberBookingRequirementDtoTranslator.translate(null));
	}

	@Test
	public void shouldTranslate() throws Exception {
		when(schemaDtoTranslator.translate(schema)).thenReturn(schemaDto);
		PhoneNumberBookingRequirementDto dto = phoneNumberBookingRequirementDtoTranslator.translate(requirement);
		assertNotNull(dto);
		assertEquals(new Long(1), dto.getId());
		assertEquals("name", dto.getName());
		assertEquals(dto.getSchema(),schemaDto);
	}
}
