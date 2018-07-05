package ru.argustelecom.box.nri.resources.inst;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDtoTranslator;
import ru.argustelecom.box.nri.resources.model.ParameterValue;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.model.ResourceStatus;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author a.wisniewski
 * @since 20.09.2017
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceInstanceDtoTranslatorTest {

	@Mock
	private ResourceSpecificationDtoTranslator resSpecTranslator;

	@Mock
	private ParameterValueDtoTranslator paramTranslator;

	@Mock
	private PhoneNumberDtoTranslator phoneTranslator;

	@Mock
	private LogicalResourceDtoTranslator logicalResourceDtoTranslator;

	@InjectMocks
	private ResourceInstanceDtoTranslator resTranslator;

	@Test
	public void translate_null() throws Exception {
		assertNull(resTranslator.translate(null));
	}

	@Test
	public void translate() throws Exception {
		ResourceSpecification spec = ResourceSpecification.builder().build();
		ResourceInstance child = ResourceInstance.builder().specification(spec).build();
		ParameterValue param = ParameterValue.builder().build();

		ResourceSpecificationDto specDto = ResourceSpecificationDto.builder().build();
		ResourceInstanceDto childDto = ResourceInstanceDto.builder().build();
		ParameterValueDto paramDto = ParameterValueDto.builder().build();

		when(paramTranslator.translate(param)).thenReturn(paramDto);
		when(resSpecTranslator.translate(spec)).thenReturn(specDto);
		when(logicalResourceDtoTranslator.translate(null)).thenReturn(null);

		ResourceInstance resource = ResourceInstance.builder()
				.id(1L)
				.name("resource")
				.specification(spec)
				.status(ResourceStatus.ACTIVE)
				.children(singletonList(child))
				.parameterValues(singletonList(param))
				.build();
		ResourceInstanceDto resourceDto = resTranslator.translate(resource);
		assertEquals((Long)1L, resourceDto.getId());
		assertEquals("resource", resourceDto.getName());
		assertEquals(specDto, resourceDto.getSpecification());
		assertEquals(ResourceStatus.ACTIVE, resourceDto.getStatus());
		assertEquals(childDto, resourceDto.getChildren().get(0));
		assertEquals(paramDto, resourceDto.getParameterValues().get(0));
	}
}