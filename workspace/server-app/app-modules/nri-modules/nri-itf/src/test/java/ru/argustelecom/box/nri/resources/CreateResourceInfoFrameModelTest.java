package ru.argustelecom.box.nri.resources;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.ContextMocker;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceAppService;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.model.ResourceStatus;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationRepository;
import ru.argustelecom.box.nri.resources.spec.model.ParameterDataType;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalAnswers.returnsSecondArg;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class CreateResourceInfoFrameModelTest {

	@Mock
	private ResourceInstanceAppService service;

	@Mock
	private ResourceSpecificationRepository resSpecRepository;

	@Mock
	private ParameterSpecificationDtoTranslator paramSpecTranslator;

	@Mock
	private ResourceSpecificationDtoTranslator resSpecTranslator;

	private static ParameterSpecification paramSpec = ParameterSpecification.builder().id(1L).name("Name").required(true).dataType(ParameterDataType.INTEGER).build();
	private static ParameterSpecificationDto paramSpecDto = ParameterSpecificationDto.builder().id(1L).name("Name").required(true).dataType(ParameterDataType.INTEGER).build();
	private static ResourceSpecification resSpec = ResourceSpecification.builder().id(1L).parameters(Arrays.asList(paramSpec)).isIndependent(true).build();
	private static ResourceSpecificationDto resSpecDto = ResourceSpecificationDto.builder().id(1L).parameters(Arrays.asList(paramSpecDto)).isIndependent(true).build();
	@InjectMocks
	private CreateResourceInfoFrameModel model;

	@Before
	public void setUp() throws Exception {
		paramSpec.setResourceSpecification(resSpec);

		when(resSpecRepository.findOne(any())).thenReturn(resSpec);
		when(paramSpecTranslator.translate(paramSpec)).thenReturn(paramSpecDto);
		when(resSpecTranslator.translate(resSpec)).thenReturn(resSpecDto);
	}

	@Test
	public void resourceSpecificationDTOConverterGetAsStringReturnNull() {
		UIComponent component = mock(UIComponent.class);
		FacesContext fc = mock(FacesContext.class);
		assertNull(model.getConverter().getAsString(fc, component, null));
	}

	@Test
	public void resourceSpecificationDTOConverterGetAsStringReturn() {
		UIComponent component = mock(UIComponent.class);
		FacesContext fc = mock(FacesContext.class);
		String asString = model.getConverter().getAsString(fc, component, ParameterSpecificationDto.builder().id(1L).build());
		assertNotNull(asString);
		assertEquals("1", asString);

	}

	@Test
	public void completeSpecNullIncrementParams() {
		ResourceInstanceDto parent = ResourceInstanceDto.builder().id(1L).name("name").status(ResourceStatus.DISABLED).build();
		model.preRender(parent, 1L);

		model.setIncrementParams(null);
		List<ParameterSpecificationDto> list = model.completeSpec("S");
		assertNotNull(list);
		assertEquals(0, list.size());

	}

	@Test
	public void completeSpecNoMatches() {
		ResourceInstanceDto parent = ResourceInstanceDto.builder().id(1L).name("name").status(ResourceStatus.DISABLED).build();
		model.preRender(parent, 1L);

		List<ParameterSpecificationDto> list = model.completeSpec("z");
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	@Test
	public void completeSpecAlreadyInclude() {
		ResourceInstanceDto parent = ResourceInstanceDto.builder().id(1L).name("name").status(ResourceStatus.DISABLED).build();
		model.preRender(parent, 1L);

		model.setIncrementParams(Arrays.asList(model.getParameters().get(0).getSpecification()));
		List<ParameterSpecificationDto> list = model.completeSpec("Int");
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	@Test
	public void completeSpecFoundOne() {
		ResourceInstanceDto parent = ResourceInstanceDto.builder().id(1L).name("name").status(ResourceStatus.DISABLED).build();
		model.preRender(parent, 1L);

		List<ParameterSpecificationDto> list = model.completeSpec("nam");
		assertNotNull(list);
		assertEquals(1, list.size());
	}

	@Test
	public void shouldGetAllStatuses() {
		List<ResourceStatus> rs = new ArrayList<>(Arrays.asList(model.getAllStatuses()));
		assertEquals(4, rs.size());
	}

	@Test
	public void preRender() {
		model.setIncrementParams(null);
		model.setInitialResNumber(2);
		model.setNewResourcesNumber(3);
		model.setShouldIncrementName(false);

		ResourceInstanceDto parent = ResourceInstanceDto.builder().id(1L).name("name").status(ResourceStatus.DISABLED).build();
		model.preRender(parent, 1L);

		assertNotNull(model.getResource());
		assertNotNull(model.getSpecification());

		assertNotNull(model.getParentResource());
		assertEquals(parent, model.getParentResource());
		assertNotNull(model.getParameters());
		assertEquals(1, model.getParameters().size());

		//Проверим что параметры восстановились по умолчанию
		assertEquals(new Integer(1), model.getInitialResNumber());
		assertEquals(new Integer(1), model.getNewResourcesNumber());
		assertEquals(new Boolean(true), model.getShouldIncrementName());
		assertNotNull(model.getIncrementParams());
		assertEquals(0, model.getIncrementParams().size());
	}

	@Test
	public void createResourceRequiredParam() {
		FacesContext context = ContextMocker.mockFacesContext();

		ResourceInstanceDto parent = ResourceInstanceDto.builder().id(1L).name("name").status(ResourceStatus.DISABLED).build();
		model.preRender(parent, 1L);
		model.setIncrementParams(null);
		model.createResource();
		BaseMatcher<FacesMessage> matcher = new BaseMatcher<FacesMessage>() {
			@Override
			public void describeTo(Description description) {
				// пустота
			}

			@Override
			public boolean matches(Object item) {
				FacesMessage actual = (FacesMessage) item;
				return actual.getSummary().equals("Не все обязательные параметры заполнены");
			}
		};

		verify(context).addMessage(eq(null), argThat(matcher));
	}

	@Test
	public void createResourceOne() {
		FacesContext context = ContextMocker.mockFacesContext();
		model.preRender(null, 1L);
		model.getParameters().get(0).setValue("1");

		when(service.createResource(any(), any())).then(returnsFirstArg());
		model.createResource();

		BaseMatcher<FacesMessage> matcher = new BaseMatcher<FacesMessage>() {
			@Override
			public void describeTo(Description description) {
				// пустота
			}

			@Override
			public boolean matches(Object item) {
				FacesMessage actual = (FacesMessage) item;
				return actual.getSummary().equals("Не все обязательные параметры заполнены");
			}
		};

		verify(context, times(0)).addMessage(eq(null), argThat(matcher));
		verify(service, atLeastOnce()).createResource(any(), any());
	}

	@Test
	public void createResourceTwoWithParent() {
		FacesContext context = ContextMocker.mockFacesContext();

		ResourceInstanceDto parent = ResourceInstanceDto.builder()
				.id(1L)
				.name("name")
				.specification(ResourceSpecificationDto.builder().isIndependent(true).build())
				.status(ResourceStatus.DISABLED)
				.build();
		model.preRender(parent, 1L);
		model.setIncrementParams(Arrays.asList(model.getParameters().get(0).getSpecification()));
		model.setNewResourcesNumber(2);
		model.setShouldIncrementName(true);

		when(service.createResource(any(), any())).then(returnsSecondArg());
		model.createResource();


		BaseMatcher<FacesMessage> matcher = new BaseMatcher<FacesMessage>() {
			@Override
			public void describeTo(Description description) {
				// пустота
			}

			@Override
			public boolean matches(Object item) {
				FacesMessage actual = (FacesMessage) item;
				return actual.getSummary().equals("Не все обязательные параметры заполнены");
			}
		};
		verify(context, times(0)).addMessage(eq(null), argThat(matcher));
		verify(service, atLeastOnce()).createResource(any(), any());
	}

	@Test
	public void resourceSpecificationDTOConverterGetAsObjectNull() {
		UIComponent component = mock(UIComponent.class);
		FacesContext fc = mock(FacesContext.class);
		assertNull(model.getConverter().getAsObject(fc, component, null));
	}


	@Test
	public void resourceSpecificationDTOConverterGetAsObjectSpaceString() {
		UIComponent component = mock(UIComponent.class);
		FacesContext fc = mock(FacesContext.class);
		assertNull(model.getConverter().getAsObject(fc, component, " "));
	}

	@Test(expected = ConverterException.class)
	public void resourceSpecificationDTOConverterGetAsObjectNumberFormatException() {
		UIComponent component = mock(UIComponent.class);
		FacesContext fc = mock(FacesContext.class);

		ResourceInstanceDto parent = ResourceInstanceDto.builder().id(1L).name("name").status(ResourceStatus.DISABLED).build();
		model.preRender(parent, 1L);

		model.getConverter().getAsObject(fc, component, "1sdfsfL");
	}

	@Test
	public void resourceSpecificationDTOConverterGetAsObject() {
		UIComponent component = mock(UIComponent.class);
		FacesContext fc = mock(FacesContext.class);

		ResourceInstanceDto parent = ResourceInstanceDto.builder().id(1L).name("name").status(ResourceStatus.DISABLED).build();
		model.preRender(parent, 1L);

		ParameterSpecificationDto obj = (ParameterSpecificationDto) model.getConverter().getAsObject(fc, component, "1");
		assertNotNull(obj);
		assertNotNull(obj.getId());
		assertEquals(new Long(1), obj.getId());
	}

	@Test
	public void shouldReturnListOfIncrementableTypes() throws Exception {
		List<ParameterDataType> types = CreateResourceInfoFrameModel.getINCREMENTABLE_DATA_TYPES();
		assertNotNull(types);
		assertFalse(types.isEmpty());
		assertTrue(types.size() == 3);
	}
}