package ru.argustelecom.box.nri.resources.requirements;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.nri.resources.ParameterSpecificationRepository;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by b.bazarov on 12.10.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ParameterSpecificationAppServiceTest {

	@Mock
	ParameterSpecificationDtoTranslator parameterSpecificationDtoTranslator;

	@Mock
	ParameterSpecificationRepository parameterSpecificationRepository;

	@InjectMocks
	private ParameterSpecificationAppService service;

	@Test
	public void getAllParameterSpecification(){

		when(parameterSpecificationRepository.findAll()).thenReturn(Arrays.asList(ParameterSpecification.builder().build()));
		when(parameterSpecificationDtoTranslator.translate(any())).thenReturn(ParameterSpecificationDto.builder().build());
		List<ParameterSpecificationDto> list = service.getAllParameterSpecification();
		assertNotNull(list);
		assertEquals(1,list.size());

	}

}