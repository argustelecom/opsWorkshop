package ru.argustelecom.box.nri.logicalresources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.ContextMocker;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationRepository;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author b.bazarov
 * @since 07.11.2017
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberCreationDialogModelTest {

	@Mock
	private PhoneNumberSpecificationRepository phoneNumberSpecificationRepository;

	@Mock
	private PhoneNumberAppService phoneService;

	@Mock
	private PhoneNumberPoolAppService poolService;

	@InjectMocks
	private PhoneNumberCreationDialogModel model;

	@Before
	public void setUp() throws Exception {
		ContextMocker.mockFacesContext();
	}

	@Test(expected = ValidatorException.class)
	public void nameValidatorBlankName() {
		PhoneNumberPoolDto pool = PhoneNumberPoolDto.builder().id(1L).phoneNumbers(Arrays.asList(PhoneNumberDto.builder().name("123").build()
				, PhoneNumberDto.builder().name("124").build())).build();
		UIComponent component = mock(UIComponent.class);
		FacesContext fc = mock(FacesContext.class);
		model.init(pool);
		model.nameValidator(fc, component, "");

	}

	@Test(expected = ValidatorException.class)
	public void nameValidatorSameName() {
		when(phoneService.findAllNotDeletedPhoneDigits()).thenReturn(Arrays.asList("123", "124"));
		PhoneNumberPoolDto pool = PhoneNumberPoolDto.builder().id(1L).build();
		UIComponent component = mock(UIComponent.class);
		FacesContext fc = mock(FacesContext.class);
		model.init(pool);
		model.nameValidator(fc, component, "124");

	}

	@Test
	public void shouldDoNameValidator() {
		PhoneNumberPoolDto pool = PhoneNumberPoolDto.builder().id(1L).phoneNumbers(Arrays.asList(PhoneNumberDto.builder().name("123").build()
				, PhoneNumberDto.builder().name("124").build())).build();
		UIComponent component = mock(UIComponent.class);
		FacesContext fc = mock(FacesContext.class);
		model.init(pool);
		model.nameValidator(fc, component, "125");
	}

	@Test
	public void shouldDoNothingWhenNameIsNull() throws Exception {
		model.nameValidator(null, null, null);
	}

	@Test
	public void shouldCreateNewPhoneNumberWhenDialogOpens() throws Exception {
		when(phoneNumberSpecificationRepository.getAllSpecs()).thenReturn(new ArrayList<>());
		model.init(null);
		PhoneNumberDto newPhoneNumber = model.getNewPhone();

		assertNotNull(newPhoneNumber);

		assertNull(newPhoneNumber.getId());
		assertNull(newPhoneNumber.getName());
	}

	@Test
	public void shouldGenerateNumbers() throws Exception {
		List<PhoneNumberDto> listOfNumbers = Arrays.asList(PhoneNumberDto.builder().id(1L).build(), PhoneNumberDto.builder().id(2L).build(), PhoneNumberDto.builder().id(3L).build());
		when(poolService.generatePhoneNumbers(any(), any(), any(), any())).thenReturn(PhoneNumberPoolDto.builder().phoneNumbers(listOfNumbers).build());
		model.setAfterGenerate(System.out::println);
		model.setPool(PhoneNumberPoolDto.builder().id(1L).build());
		model.setNewPhoneSpec(new PhoneNumberSpecification(1L));
		model.setFromPhone("1");
		model.setToPhone("0");
		model.generateNumbers();

		verify(poolService, atLeastOnce()).generatePhoneNumbers(any(), any(), any(), any());
	}

	@Test
	public void shouldCatchExceptionWhenGeneratingNumbers() throws Exception {
		when(poolService.generatePhoneNumbers(any(), any(), any(), any())).thenThrow(new BusinessExceptionWithoutRollback());
		model.setPool(PhoneNumberPoolDto.builder().id(1L).build());
		model.setNewPhoneSpec(new PhoneNumberSpecification(1L));
		model.generateNumbers();
	}

	@Test
	public void shouldCreateNumber() throws Exception {
		model.setNewPhone(PhoneNumberDto.builder().build());
		model.setNewPhoneSpec(new PhoneNumberSpecification(1L));
		model.setOnCreate((pool, spec) -> System.out.println(pool.toString() + spec.toString()));
		model.create();
	}
}