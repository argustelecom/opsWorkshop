package ru.argustelecom.box.nri.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.nri.resources.requirements.RequiredItemDto;
import ru.argustelecom.box.nri.resources.requirements.RequiredParameterValueAppService;
import ru.argustelecom.box.nri.resources.requirements.RequiredParameterValueDto;
import ru.argustelecom.box.nri.schema.requirements.resources.comparators.CompareAction;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.model.ParameterDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by b.bazarov on 11.10.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ParamsRequirementInfoFrameModelTest {


	@Mock
	private RequiredParameterValueAppService requiredParameterValueAppService;

	@InjectMocks
	private ParamsRequirementInfoFrameModel model;

	@Test
	public void shouldGetTypesZero() {
		List<CompareAction> list = model.getTypes();
		assertNotNull(list);
		assertEquals(0, list.size());
	}
	@Test
	public void shouldGetTypesForBoolean() {
		model.setCurrentRequiredParameterValueDto(RequiredParameterValueDto.builder()
				.parameterSpecification(ParameterSpecificationDto.builder().dataType(ParameterDataType.BOOLEAN).build()).build());
		List<CompareAction> list = model.getTypes();
		assertNotNull(list);
		assertEquals(2, list.size());
	}
	@Test
	public void shouldPreRenderNonParam() {

		model.preRender(RequiredItemDto.builder().resourceSpecification(ResourceSpecificationDto.builder().build()).build());
		assertNotNull(model.getRequiredItem());
		assertNotNull(model.getPossibleParamSpecification());
		assertEquals(0,model.getPossibleParamSpecification().size());
		assertNotNull(model.getCurrentRequiredParameterValueDto());
	}
	@Test
	public void shouldPreRenderOnePossibleParam() {
		List<ParameterSpecificationDto> list = Arrays.asList(ParameterSpecificationDto.builder().id(1L).build());
		model.preRender(RequiredItemDto.builder().resourceSpecification(ResourceSpecificationDto.builder().parameters(
				list).build()).build());
		assertNotNull(model.getRequiredItem());
		assertNotNull(model.getPossibleParamSpecification());
		assertEquals(list, model.getPossibleParamSpecification());
		assertNotNull(model.getCurrentRequiredParameterValueDto());
	}

	@Test
	public void shouldCleanCreationParams() {
		model.setCurrentRequiredParameterValueDto(RequiredParameterValueDto.builder().id(1L).value("333").build());
		model.getCurrentRequiredParameterValueDto().setId(1L);
		model.cleanCreationParams();
		assertNull(model.getCurrentRequiredParameterValueDto().getId());
		assertNull(model.getCurrentRequiredParameterValueDto().getValue());

	}

	@Test
	public void shouldSubmitSave() {
		RequiredParameterValueDto value = RequiredParameterValueDto.builder().id(1L).value("333").build();
		model.setCurrentRequiredParameterValueDto(value);
		model.submit();
		verify(requiredParameterValueAppService, times(1)).save(eq(value));
		assertNotNull(model.getCurrentRequiredParameterValueDto());
		assertNull(model.getCurrentRequiredParameterValueDto().getId());
	}

	@Test
	public void shouldSubmitCreateNew() {
		RequiredParameterValueDto value = RequiredParameterValueDto.builder().value("333").build();
		model.setCurrentRequiredParameterValueDto(value);
		model.setRequiredItem(RequiredItemDto.builder().build());
		when(requiredParameterValueAppService.create(any(), any())).thenReturn(value);
		model.submit();
		assertNotNull(model.getCurrentRequiredParameterValueDto());
		assertNull(model.getCurrentRequiredParameterValueDto().getId());
		assertEquals(1, model.getRequiredItem().getRequiredParameters().size());
	}

	@Test
	public void shouldDeleteSelected() {
		RequiredParameterValueDto value = RequiredParameterValueDto.builder().id(1L).value("333").build();
		model.setCurrentRequiredParameterValueDto(value);
		List<RequiredParameterValueDto> list = new ArrayList<>();
		list.add(value);

		model.setRequiredItem(RequiredItemDto.builder().id(1L).requiredParameters(list).build());
		model.setSelectedRequiredParameterValueDto(Arrays.asList(value));

		model.deleteSelected();

		assertNull(model.getSelectedRequiredParameterValueDto());
		verify(requiredParameterValueAppService, times(1)).remove(value);
		assertEquals(0, model.getRequiredItem().getRequiredParameters().size());
	}

	@Test
	public void shouldNotDeleteSelectedNullSelected() {
		RequiredParameterValueDto value = RequiredParameterValueDto.builder().id(1L).value("333").build();
		model.setCurrentRequiredParameterValueDto(value);
		model.setRequiredItem(RequiredItemDto.builder().requiredParameters(Collections.singletonList(value)).build());
		model.setSelectedRequiredParameterValueDto(null);

		model.deleteSelected();
		verify(requiredParameterValueAppService, times(0)).remove(any());
	}
	@Test
	public void shouldDoNothingSubmit(){
		model.setCurrentRequiredParameterValueDto(null);
		model.setRequiredItem(RequiredItemDto.builder().build());
		model.submit();
		verify(requiredParameterValueAppService, times(0)).save(any());
		verify(requiredParameterValueAppService, times(0)).create(any(),any());
	}

}