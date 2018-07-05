package ru.argustelecom.box.nri.resources.requirements;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.nri.resources.ParameterSpecificationRepository;
import ru.argustelecom.box.nri.schema.requirements.resources.RequiredItemRepository;
import ru.argustelecom.box.nri.schema.requirements.resources.RequiredParameterValueRepository;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredParameterValue;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by b.bazarov on 12.10.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequiredParameterValueAppServiceTest {

	@Mock
	private IdSequenceService idSequenceService;

	@Mock
	private RequiredParameterValueRepository requiredParameterValueRepository;

	@Mock
	private ParameterSpecificationRepository parameterSpecificationRepository;

	@Mock
	private RequiredItemRepository requiredItemRepository;

	@Mock
	private RequiredParameterValueDtoTranslator requiredParameterValueDtoTranslator;
	
	@InjectMocks
	private RequiredParameterValueAppService service;

	@Test
	public void shouldCreate(){
		RequiredParameterValueDto dto = RequiredParameterValueDto.builder().parameterSpecification(
				ParameterSpecificationDto.builder().id(1L).build()).id(1L).build();

		RequiredItemDto riDto = RequiredItemDto.builder().id(4L).build();
		service.create(dto,riDto);
		verify(requiredParameterValueRepository,times(1)).create(any(),eq(riDto.getId()));

	}
	@Test
	public void shouldNotSave(){
		when(requiredParameterValueRepository.findById(eq(1L))).thenReturn(null);
		service.save(RequiredParameterValueDto.builder().id(1L).build());
		verify(requiredParameterValueRepository,times(0)).save(any());
	}

	@Test
	public void shouldSave(){
		when(requiredParameterValueRepository.findById(eq(1L))).thenReturn(RequiredParameterValue.builder().build());
		service.save(RequiredParameterValueDto.builder().id(1L).parameterSpecification(ParameterSpecificationDto.builder().id(2L).build()).build());
		verify(requiredParameterValueRepository,times(1)).save(any());
	}

	@Test
	public void shouldRemove(){

		service.remove(RequiredParameterValueDto.builder().id(1L).build());
		verify(requiredParameterValueRepository,times(1)).delete(eq(1L));
	}
}